package xyz.s4i5.userservice.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import xyz.s4i5.userservice.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Named("mapWithoutPassword")
    @Mapping(target = "password", ignore = true)
    User toUser(UserDto userDto);
    UserDto toUserDto(User user);
}
