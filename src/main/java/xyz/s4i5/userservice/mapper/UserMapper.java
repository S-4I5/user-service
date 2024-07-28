package xyz.s4i5.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserDto;
import xyz.s4i5.userservice.model.entity.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserDto createUserDto);

    UserDto toUserDto(User user);

    void update(@MappingTarget User user, UpdateUserDto updateUserDto);
}
