package xyz.s4i5.userservice.user.requests;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.NonNull;

@JsonSerialize
public record CreateUserRequest(
        @NotNull String email,
        @NotNull String login,
        @NotNull String password
) {
}
