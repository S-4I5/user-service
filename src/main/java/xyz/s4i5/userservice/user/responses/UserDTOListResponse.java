package xyz.s4i5.userservice.user.responses;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.List;

public record UserDTOListResponse(
        @Parameter(description = "Users list info")
        List<UserDTOResponse> users
) {
}
