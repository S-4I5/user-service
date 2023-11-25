package xyz.s4i5.userservice.user.responses;

import xyz.s4i5.userservice.user.dto.UserDTO;

public record ChangedFieldResponse(
        UserDTO changed_fields
) {
}
