package xyz.s4i5.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserDto;
import xyz.s4i5.userservice.model.entity.role.Role;
import xyz.s4i5.userservice.model.entity.role.RoleName;
import xyz.s4i5.userservice.model.entity.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoleNamesToRoles")
    User toUser(CreateUserDto dto);

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToRoleNames")
    UserDto toUserDto(User user);

    @Mapping(source = "updateUserDto.roles", target = "user.roles", qualifiedByName = "mapRoleNamesToRoles")
    void update(@MappingTarget User user, UpdateUserDto updateUserDto);

    @Named("mapRolesToRoleNames")
    default List<RoleName> mapRolesToRoleNames(List<Role> roles) {
        return roles.stream().map(Role::getRoleName).collect(Collectors.toList());
    }

    @Named("mapRoleNamesToRoles")
    default List<Role> mapRoleNamesToRoles(List<RoleName> roles) {
        if (roles == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(roles.stream().map(name -> Role.builder()
                .roleName(name)
                .build()).toList());
    }
}
