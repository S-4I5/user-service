package xyz.s4i5.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import xyz.s4i5.userservice.model.entity.role.RoleName;

import java.util.List;

@Data
@Builder
public class CreateUserDto {
    @Parameter(description = "User email")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotNull(message = "Email cannot be empty")
    String email;

    @Parameter(description = "User login")
    @NotNull
    String login;

    @Parameter(description = "User password")
    @NotNull
    String password;

    @Parameter(description = "User full name")
    private String fullName;

    @Parameter(description = "User roles")
    private List<RoleName> roles;
}
