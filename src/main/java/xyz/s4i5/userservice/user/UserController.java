package xyz.s4i5.userservice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.request.CreateUserRequest;

@Controller()
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(
            @RequestBody CreateUserRequest request
    ) {
        var result = userService.createUser(request.email(), request.password());

        return (result.map(
                dto -> ResponseEntity.status(201).body(dto)).orElseGet(
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
    public ResponseEntity<UserDTO> getUser(
            @PathVariable String id
    ) {
        var result = userService.getUser(id);

        return (result.map(ResponseEntity::ok).orElseGet(
                () -> ResponseEntity.noContent().build())
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @RequestBody UserDTO userDTO
    ) {
        var result = userService.updateUser(userDTO, id);

        return (result.map(
                dto -> ResponseEntity.status(201).body(dto)).orElseGet(
                () -> ResponseEntity.badRequest().build())
        );
    }
}
