package xyz.s4i5.userservice.user.requests;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record CreateUserRequest(
        String email,
        String login,
        String password
) {
}
