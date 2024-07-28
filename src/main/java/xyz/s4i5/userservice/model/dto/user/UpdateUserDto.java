package xyz.s4i5.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import xyz.s4i5.userservice.model.entity.role.Role;
import xyz.s4i5.userservice.model.entity.role.RoleName;

import java.util.List;

@Data
@Builder
public class UpdateUserDto {
    @Parameter(description = "User login")
    @NotEmpty(message = "Login cannot be empty")
    private String login;

    @Parameter(description = "User email")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotEmpty(message = "Email cannot be empty")
    private String email;

    @Parameter(description = "User full name")
    private String fullName;

    @Parameter(description = "User roles")
    private List<RoleName> roles;
}
