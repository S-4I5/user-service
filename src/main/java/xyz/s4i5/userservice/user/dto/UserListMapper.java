package xyz.s4i5.userservice.user.dto;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import xyz.s4i5.userservice.user.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserListMapper {
    @IterableMapping(qualifiedByName="mapWithoutPassword")
    List<User> toUserList(List<UserDto> userDto);
    List<UserDto> toUserDtoList(List<User> user);
}
