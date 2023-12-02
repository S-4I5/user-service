package xyz.s4i5.userservice.user.dto;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import xyz.s4i5.userservice.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Named("mapWithoutPassword")
    @Mapping(target = "password", ignore = true)
    User toUser(UserDto userDto);
    UserDto toUserDto(User user);
    @IterableMapping(qualifiedByName="mapWithoutPassword")
    List<User> toUserList(List<UserDto> userDto);
    List<UserDto> toUserDtoList(List<User> user);
}
