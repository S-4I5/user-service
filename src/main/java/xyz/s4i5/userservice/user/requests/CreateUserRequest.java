package xyz.s4i5.userservice.user.requests;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

@JsonSerialize
public record CreateUserRequest(
        @Parameter(description = "User email")
        @Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
        @NotNull(message = "Email cannot be empty")
        String email,
        @Parameter(description = "User login")
        @NotNull String login,
        @Parameter(description = "User password")
        @NotNull String password
) {
}
