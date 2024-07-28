package xyz.s4i5.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import xyz.s4i5.userservice.model.entity.user.Role;

import java.util.List;

@Data
@Builder
public class CreateUserDto {
    @Parameter(description = "User email")
    @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotNull(message = "Email cannot be empty")
    private String email;

    @Parameter(description = "User login")
    @NotNull
    private String login;

    @Parameter(description = "User password")
    @NotNull
    private String password;

    private String fullName;

    private List<Role> roles;
}
