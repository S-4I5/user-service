package xyz.s4i5.userservice.user.responses;

import java.util.List;

public record UserDTOListResponse(
        List<UserDTOResponse> users
) {
}
