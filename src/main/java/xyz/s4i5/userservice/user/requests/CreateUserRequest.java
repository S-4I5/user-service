package xyz.s4i5.userservice.user.requests;

public record CreateUserRequest(
        String email,
        String password
) {
}
