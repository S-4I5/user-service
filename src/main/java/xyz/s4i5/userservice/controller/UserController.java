package xyz.s4i5.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserSearchDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserDto;
import xyz.s4i5.userservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping(UserController.ROOT_URI)
public class UserController {
    protected static final String ROOT_URI = "/user";
    protected static final String DELETE_URI = "/{id}";
    protected static final String UPDATE_URI = "/{id}";
    protected static final String GET_URI = "/{id}";
    protected static final String SEARCH_URI = "/search";

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
        return userService.createUser(request);
    }

    @Operation(
            summary = "Delete user",
            description = "Will delete user with given id"
    )
    @ApiResponse(responseCode = "204", description = "Returned user deleted")
    @ApiResponse(responseCode = "400", description = "User not found", content = @Content(schema = @Schema(hidden = true)))
    @DeleteMapping(DELETE_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(
            @Parameter(description = "User with this id should be deleted")
            @PathVariable String id
    ) {
        userService.deleteUser(id);
    }

    @Operation(
            summary = "Get user",
            description = "Will return user with given id"
    )
    @ApiResponse(responseCode = "200", description = "User returned",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Users= not found", content = @Content(schema = @Schema(hidden = true)))
    @GetMapping(GET_URI)
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
    @PostMapping(SEARCH_URI)
    public Page<UserDto> searchForUsers(
            @RequestBody UserSearchDto userSearchDto,
            Pageable pageable
    ) {
        return userService.searchUsers(userSearchDto, pageable);
    }

    @Operation(
            summary = "Update users",
            description = "Will update user with given info"
    )
    @ApiResponse(responseCode = "200", description = "User updated",
            content = @Content( schema = @Schema(implementation = UserDto.class),
                    mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Invalid username/login/request body supplied", content = @Content(schema = @Schema(hidden = true)))
    @PatchMapping(UPDATE_URI)
    public UserDto updateUser(
            @Parameter(description = "User with this id should be updated")
            @PathVariable String id,
            @Parameter(description = "List of fields for update", required = true)
            @RequestBody UpdateUserDto updateUserDto
    ) {
        return userService.updateUser(updateUserDto, id);
    }
}
