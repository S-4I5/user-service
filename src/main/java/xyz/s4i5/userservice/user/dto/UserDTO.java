package xyz.s4i5.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import xyz.s4i5.userservice.user.Role;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserDTO {
    private String id;
    private String login;
    private String email;
    private String fullName;
    private List<Role> roles;
}
