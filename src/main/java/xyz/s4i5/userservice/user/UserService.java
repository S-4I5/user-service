package xyz.s4i5.userservice.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import xyz.s4i5.userservice.encoder.PasswordEncoder;
import xyz.s4i5.userservice.user.dto.UpdateUserDto;
import xyz.s4i5.userservice.user.dto.UserDto;
import xyz.s4i5.userservice.user.dto.UserListMapper;
import xyz.s4i5.userservice.user.dto.UserMapper;
import xyz.s4i5.userservice.user.exceptions.CannotCreateUserException;
import xyz.s4i5.userservice.user.exceptions.CannotUpdateUserException;
import xyz.s4i5.userservice.user.exceptions.UserNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserListMapper userListMapper;

    public UserDto createUser(String email, String login, String password){
        try {
            return userMapper.toUserDto(userRepository.save(
                        User.builder()
                                .email(email)
                                .login(login)
                                .password(passwordEncoder.encode(password))
                                .build()
            ));
        } catch (DuplicateKeyException e) {
            throw new CannotCreateUserException();
        }
    }

    public List<UserDto> getUsers(UserDto userForSearchWithoutRoles, List<Role> roles, int offset, int limit){
        System.out.println(userMapper.toUser(userForSearchWithoutRoles) + "map");

        List<User> users = userRepository.findAll(
                Example.of(userMapper.toUser(userForSearchWithoutRoles)), PageRequest.of(offset, limit)).getContent();

        if(!roles.isEmpty()){
            users = users.stream().filter(x -> {
                if(x.getRoles() == null){
                    return false;
                }
                return new HashSet<>(x.getRoles()).containsAll(roles);
            }).toList();
        }

        if(users.isEmpty()){
            throw new UserNotFoundException();
        }

        return userListMapper.toUserDtoList(users);
    }

    public UserDto getUser(String id){
        return userRepository.findById(id).map(userMapper::toUserDto).orElseThrow(UserNotFoundException::new);
    }

    @SneakyThrows
    public UserDto deleteUser(String id){

        UserDto userDto = userRepository.findById(id).map(userMapper::toUserDto).orElseThrow(UserNotFoundException::new);

        userRepository.deleteById(id);

        return userDto;
    }

    @SneakyThrows
    public UserDto updateUser(UpdateUserDto updateUserDto, String id){
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        try {
            Optional.ofNullable(updateUserDto.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(updateUserDto.getLogin()).ifPresent(user::setLogin);
            Optional.ofNullable(updateUserDto.getFullName()).ifPresent(user::setFullName);
            Optional.ofNullable(updateUserDto.getRoles()).ifPresent(user::setRoles);

            userRepository.save(user);
        } catch (DuplicateKeyException e){
            throw new CannotUpdateUserException();
        }

        return userMapper.toUserDto(user);
    }
}
