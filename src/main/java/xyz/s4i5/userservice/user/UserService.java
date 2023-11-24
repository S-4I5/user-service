package xyz.s4i5.userservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.s4i5.userservice.encoder.PasswordEncoder;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.dto.UserDTOMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public Optional<UserDTO> createUser(String email, String password){

        try {
            return Optional.of(
                    userDTOMapper.apply(userRepository.save(
                        User.builder()
                                .email(email)
                                .password(passwordEncoder.encode(password))
                                .build()
            )));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserDTO> getUser(String id){
        return userRepository.findById(id).map(userDTOMapper);
    }

    public boolean deleteUser(String id){

        if(userRepository.findById(id).isEmpty()){
            return false;
        }

        userRepository.deleteById(id);

        return true;
    }

    public Optional<UserDTO> updateUser(UserDTO userDTO, String id){
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        Optional.ofNullable(userDTO.getEmail()).ifPresent(user.get()::setEmail);
        Optional.ofNullable(userDTO.getLogin()).ifPresent(user.get()::setLogin);
        Optional.ofNullable(userDTO.getEmail()).ifPresent(user.get()::setEmail);
        Optional.ofNullable(userDTO.getRoles()).ifPresent(user.get()::setRoles);

        userRepository.save(user.get());

        return Optional.of(userDTO);
    }
}
