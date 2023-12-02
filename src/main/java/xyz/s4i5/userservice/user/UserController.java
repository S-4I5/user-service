package xyz.s4i5.userservice.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.user.dto.CreateUserDto;
import xyz.s4i5.userservice.user.dto.UpdateUserDto;
import xyz.s4i5.userservice.user.dto.UserDto;

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
    @ApiResponse(responseCode = "201", description = "User created",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid username/login/request body supplied", content = @Content(schema = @Schema(hidden = true)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(
            @Parameter(description = "User with this cred. should be created", required = true)
            @Valid @RequestBody CreateUserDto request
    ) {
        return userService.createUser(request.getEmail(), request.getLogin(), request.getPassword());
    }

    @Operation(
            summary = "Delete user",
            description = "Will delete user with given id"
    )
    @ApiResponse(responseCode = "201", description = "Returned user deleted",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Users not found", content = @Content(schema = @Schema(hidden = true)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDto deleteUser(
            @Parameter(description = "User with this id should be deleted")
            @PathVariable String id
    ) {
        return userService.deleteUser(id);
    }

    @Operation(
            summary = "Get user",
            description = "Will return user with given id"
    )
    @ApiResponse(responseCode = "200", description = "User returned",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Users not found", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping("/{id}")
    public UserDto getUser(
            @Parameter(description = "User with this id should be returned", required = true)
            @PathVariable String id
    ) {
        return userService.getUser(id);
    }

    @Operation(
            summary = "Get users",
            description = "Will return list of users filtered by given parameters"
    )
    @ApiResponse(responseCode = "200", description = "Users returned",
            content = @Content( array = @ArraySchema(schema = @Schema(implementation = UserDto.class)),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "Users not found", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping
    public List<UserDto> getUsersWithParams(
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "login", required = false) String login,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "roles", required = false, defaultValue = "") List<Role> roles,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit,
            @RequestParam(name = "offset", required = false, defaultValue = "0") int offset
    ) {
        return userService.getUsers(UserDto.builder()
                .id(id)
                .login(login)
                .email(email)
                .fullName(fullName)
                .build(), roles, offset, limit);
    }

    @Operation(
            summary = "Update users",
            description = "Will update user with given info"
    )
    @ApiResponse(responseCode = "201", description = "User updated",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(hidden = true)))
    @ApiResponse(responseCode = "400", description = "Invalid username/login/request body supplied", content = @Content(schema = @Schema(hidden = true)))
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public UserDto updateUser(
            @Parameter(description = "User with this id should be updated")
            @PathVariable String id,
            @Parameter(description = "List of fields for update", required = true)
            @RequestBody UpdateUserDto updateUserDto
    ) {
        return userService.updateUser(updateUserDto, id);
    }
}
