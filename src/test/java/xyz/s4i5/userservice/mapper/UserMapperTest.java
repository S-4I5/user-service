package xyz.s4i5.userservice.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.dto.user.UserDto;
import xyz.s4i5.userservice.model.entity.role.Role;
import xyz.s4i5.userservice.model.entity.role.RoleName;
import xyz.s4i5.userservice.model.entity.user.User;

import java.util.List;

@SpringBootTest(
        classes = {
                UserMapperImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class UserMapperTest {
    @Autowired
    private UserMapper mapper;

    @Test
    void update() {
        var givenUser = User.builder().build();

        var updateUserDto = UpdateUserDto.builder()
                .login("login")
                .fullName("fullName")
                .email("email")
                .roles(
                        List.of(
                                RoleName.APP2_USER,
                                RoleName.APP1_USER,
                                RoleName.APP2_ADMIN,
                                RoleName.APP1_ADMIN
                        )
                )
                .build();


        mapper.update(givenUser, updateUserDto);


        System.out.println(givenUser);
    }

    @Test
    void toUser() {
        var createUserDto = CreateUserDto.builder()
                .login("login")
                .fullName("fullName")
                .email("email")
                .roles(
                        List.of(
                                RoleName.APP2_USER,
                                RoleName.APP1_USER,
                                RoleName.APP2_ADMIN,
                                RoleName.APP1_ADMIN
                        )
                )
                .build();


        var actual = mapper.toUser(createUserDto);

        System.out.println(actual);
    }

    @Test
    void toDto() {
        var givenUser = User.builder()
                .login("login")
                .fullName("fullName")
                .email("email")
                .build();
        givenUser.setRoles(List.of(
                Role.builder()
                        .user(givenUser)
                        .roleName(RoleName.APP1_USER)
                        .build(),
                Role.builder()
                        .user(givenUser)
                        .roleName(RoleName.APP2_USER)
                        .build(),
                Role.builder()
                        .user(givenUser)
                        .roleName(RoleName.APP1_ADMIN)
                        .build(),
                Role.builder()
                        .user(givenUser)
                        .roleName(RoleName.APP2_ADMIN)
                        .build()
        ));


        var actual = mapper.toUserDto(givenUser);


        System.out.println(actual);
    }
}
