package xyz.s4i5.userservice.user.request;

public record CreateUserRequest(
        String email,
        String password
) {
}
