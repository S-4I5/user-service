package xyz.s4i5.userservice.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.requests.CreateUserRequest;
import xyz.s4i5.userservice.user.responses.ChangedFieldResponse;
import xyz.s4i5.userservice.user.responses.UserDTOListResponse;
import xyz.s4i5.userservice.user.responses.UserDTOResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping()
    public ResponseEntity<UserDTOResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        var result = userService.createUser(request.email(), request.login(), request.password());

        return (result.map(
                dto -> ResponseEntity.status(201).body(new UserDTOResponse(dto))).orElseGet(
                        () -> ResponseEntity.badRequest().build())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @NotNull @PathVariable String id
    ) {
        boolean result = userService.deleteUser(id);

        return (result ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> getUser(
            @NotNull @PathVariable String id
    ) {
        var result = userService.getUser(id);

        return (result.map(
                dto -> ResponseEntity.ok(new UserDTOResponse(dto))).orElseGet(
                () -> ResponseEntity.noContent().build())
        );
    }

    @GetMapping()
    public ResponseEntity<UserDTOListResponse> getUser(
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset
    ) {
        var result = userService.getUsers(offset, limit);

        return (result.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(new UserDTOListResponse(result.stream().map(UserDTOResponse::new).toList()))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChangedFieldResponse> updateUser(
            @NotNull @PathVariable String id,
            @RequestBody UserDTO userDTO
    ) {
        var result = userService.updateUser(userDTO, id);

        return (result.map(
                dto -> ResponseEntity.accepted().body(new ChangedFieldResponse(dto))).orElseGet(
                () -> ResponseEntity.badRequest().build())
        );
    }
}
