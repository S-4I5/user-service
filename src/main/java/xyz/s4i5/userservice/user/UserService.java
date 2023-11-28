package xyz.s4i5.userservice.user;

import com.mongodb.MongoWriteException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import xyz.s4i5.userservice.encoder.PasswordEncoder;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.dto.UserDTOMapper;
import xyz.s4i5.userservice.user.exceptions.CannotCreateUserException;
import xyz.s4i5.userservice.user.exceptions.CannotUpdateUserException;
import xyz.s4i5.userservice.user.exceptions.UserNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserDTO> createUser(String email, String login, String password){

        try {
            return Optional.of(
                    userDTOMapper.apply(userRepository.save(
                        User.builder()
                                .email(email)
                                .login(login)
                                .password(passwordEncoder.encode(password))
                                .build()
            )));
        } catch (Exception e) {
            throw new CannotCreateUserException();
        }
    }

    public List<UserDTO> getUsers(String id, String login, String email, String fullName, List<Role> roles,
                                  int offset, int limit){


        User ex = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .fullName(fullName)
                .build();

        Example<User> example = Example.of(ex);

        Page<User> userPage = userRepository.findAll(example, PageRequest.of(offset, limit));

        if(userPage.getContent().isEmpty()){
            throw new UserNotFoundException();
        }

        return userPage.getContent().stream().map(userDTOMapper).filter(x -> {
            if(x.getRoles() == null)
                return false;
            return new HashSet<>(x.getRoles()).containsAll(roles);
        }).toList();
    }

    public Optional<UserDTO> getUser(String id){
        Optional<UserDTO> userDTO = userRepository.findById(id).map(userDTOMapper);

        if(userDTO.isEmpty()){
            throw new UserNotFoundException();
        }

        return userDTO;
    }

    @SneakyThrows
    public boolean deleteUser(String id){

        if(userRepository.findById(id).isEmpty()){
            throw new UserNotFoundException();
        }

        userRepository.deleteById(id);

        return true;
    }

    @SneakyThrows
    public Optional<UserDTO> updateUser(UserDTO userDTO, String id){

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        try {
            Optional.ofNullable(userDTO.getEmail()).ifPresent(user.get()::setEmail);
            Optional.ofNullable(userDTO.getLogin()).ifPresent(user.get()::setLogin);
            Optional.ofNullable(userDTO.getFullName()).ifPresent(user.get()::setFullName);
            Optional.ofNullable(userDTO.getRoles()).ifPresent(user.get()::setRoles);

            userRepository.save(user.get());
        } catch (Exception e){
            throw new CannotUpdateUserException();
        }

        return Optional.of(userDTO);
    }
}
