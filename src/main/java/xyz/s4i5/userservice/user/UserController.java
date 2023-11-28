package xyz.s4i5.userservice.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.exceptions.CannotCreateUserException;
import xyz.s4i5.userservice.user.exceptions.UserNotFoundException;
import xyz.s4i5.userservice.user.requests.CreateUserRequest;
import xyz.s4i5.userservice.user.responses.ChangedFieldResponse;
import xyz.s4i5.userservice.user.responses.UserDTOListResponse;
import xyz.s4i5.userservice.user.responses.UserDTOResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create user",
            description = "Create user with email, login and password"
    )
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "400", description = "Invalid username/login supplied/request body",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CannotCreateUserException.class)
            ))
    @PostMapping()
    public ResponseEntity<UserDTOResponse> createUser(
            @Parameter(description = "User with this cred. should be created", required = true)
            @Valid @RequestBody CreateUserRequest request
    ) {


        var result = userService.createUser(request.email(), request.login(), request.password());

        return (result.map(
                dto -> ResponseEntity.status(201).body(new UserDTOResponse(dto))).orElseGet(
                        () -> ResponseEntity.badRequest().build())
        );
    }

    @Operation(
            summary = "Delete user",
            description = "Will delete user with given id"
    )
    @ApiResponse(responseCode = "204", description = "User deleted")
    @ApiResponse(responseCode = "404", description = "Users not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserNotFoundException.class)
            ))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User with this id should be deleted", required = true)
            @NotNull @PathVariable String id
    ) {
        boolean result = userService.deleteUser(id);

        return (result ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get user",
            description = "Will return user with given id"
    )
    @ApiResponse(responseCode = "200", description = "User returned")
    @ApiResponse(responseCode = "404", description = "Users not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserNotFoundException.class)
            ))
    @GetMapping("/{id}")
    public ResponseEntity<UserDTOResponse> getUser(
            @Parameter(description = "User with this id should be returned", required = true)
            @NotNull @PathVariable String id
    ) {
        var result = userService.getUser(id);

        return (result.map(
                dto -> ResponseEntity.ok(new UserDTOResponse(dto))).orElseGet(
                () -> ResponseEntity.notFound().build())
        );
    }

    @Operation(
            summary = "Get users",
            description = "Will return list of users"
    )
    @ApiResponse(responseCode = "200", description = "Users returned")
    @ApiResponse(responseCode = "404", description = "Users not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserNotFoundException.class)
            ))
    @GetMapping("")
    public ResponseEntity<UserDTOListResponse> getUsersWithParams(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "login", required = false) String login,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "roles", required = false) List<Role> roles,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset
    ) {
        if(roles == null){
            roles = List.of();
        }

        var result = userService.getUsers(id, login, email, fullName, roles, offset, limit);

        return (result.isEmpty() ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok(new UserDTOListResponse(result.stream().map(UserDTOResponse::new).toList()))
        );
    }

    @Operation(
            summary = "Update users",
            description = "Will update user with given info"
    )
    @ApiResponse(responseCode = "201", description = "Users updated")
    @ApiResponse(responseCode = "404", description = "Users not found",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserNotFoundException.class)
            ))
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @PatchMapping("/{id}")
    public ResponseEntity<ChangedFieldResponse> updateUser(
            @Parameter(description = "User with this id should be updated", required = true)
            @NotNull @PathVariable String id,
            @Parameter(description = "List of fields for update", required = true)
            @RequestBody UserDTO userDTO
    ) {
        var result = userService.updateUser(userDTO, id);

        return (result.map(
                dto -> ResponseEntity.accepted().body(new ChangedFieldResponse(dto))).orElseGet(
                () -> ResponseEntity.badRequest().build())
        );
    }
}
