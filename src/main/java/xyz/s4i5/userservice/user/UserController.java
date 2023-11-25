package xyz.s4i5.userservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.requests.CreateUserRequest;
import xyz.s4i5.userservice.user.responses.ChangedFieldResponse;
import xyz.s4i5.userservice.user.responses.UserDTOResponse;

@Controller()
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDTOResponse> createUser(
            @RequestBody CreateUserRequest request
    ) {
        var result = userService.createUser(request.email(), request.password());

        return (result.map(
                dto -> ResponseEntity.status(201).body(new UserDTOResponse(dto))).orElseGet(
                        () -> ResponseEntity.badRequest().build())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String id
    ) {
        boolean result = userService.deleteUser(id);

        return (result ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> getUser(
            @PathVariable String id
    ) {
        var result = userService.getUser(id);

        return (result.map(
                dto -> ResponseEntity.ok(new UserDTOResponse(dto))).orElseGet(
                () -> ResponseEntity.noContent().build())
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ChangedFieldResponse> updateUser(
            @PathVariable String id,
            @RequestBody UserDTO userDTO
    ) {
        var result = userService.updateUser(userDTO, id);

        return (result.map(
                dto -> ResponseEntity.status(201).body(new ChangedFieldResponse(dto))).orElseGet(
                () -> ResponseEntity.badRequest().build())
        );
    }
}
