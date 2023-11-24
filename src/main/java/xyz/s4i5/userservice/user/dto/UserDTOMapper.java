package xyz.s4i5.userservice.user.dto;

import org.springframework.stereotype.Component;
import xyz.s4i5.userservice.user.User;

import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<User, UserDTO> {
    @Override
    public UserDTO apply(User user) {
        if(user == null){
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }
}
