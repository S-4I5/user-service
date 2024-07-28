package xyz.s4i5.userservice.model.dto.user;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

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
}
