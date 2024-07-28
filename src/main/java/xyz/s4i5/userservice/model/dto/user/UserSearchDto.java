package xyz.s4i5.userservice.model.dto.user;

import lombok.Builder;
import lombok.Data;
import xyz.s4i5.userservice.model.entity.user.Role;

import java.util.List;

@Data
@Builder
public class UserSearchDto {
    private List<Role> roles;

    private String fullName;

    private String login;

    private String email;
}
