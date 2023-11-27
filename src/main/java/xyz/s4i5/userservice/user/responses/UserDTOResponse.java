package xyz.s4i5.userservice.user.responses;

import io.swagger.v3.oas.annotations.Parameter;
import xyz.s4i5.userservice.user.dto.UserDTO;

public record UserDTOResponse(
        @Parameter(description = "User info")
        UserDTO user
) {
}
