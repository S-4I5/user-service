package xyz.s4i5.userservice.user;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.s4i5.userservice.user.dto.UserDTO;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceIntegrationTest {

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    private final String login = "tests_lover";
    private final String email = "example@gmail.com";
    private final String password = "password";

    @Autowired
    private UserService userService;

    @Test
    @Order(1)
    public void shouldSaveUser() {
        Optional<UserDTO> user = userService.createUser(email, login, password);

        assertThat(user.isPresent()).isTrue();
    }

    @Test
    @Order(2)
    public void shouldNotSaveUserWithSameLoginAndEmail() {
        Optional<UserDTO> user = userService.createUser(1 + email, login, password);
        assertThat(user.isPresent()).isFalse();

        user = userService.createUser(email, login + 1, password);
        assertThat(user.isPresent()).isFalse();
    }

    @Test
    @Order(3)
    public void shouldDeleteUser() {
        Optional<UserDTO> user = userService.createUser(1 + email, login + 1, password);
        assertThat(user.isPresent()).isTrue();

        assertThat(userService.deleteUser(user.get().getId())).isTrue();
    }

    @Test
    @Order(4)
    public void shouldUpdateUser() {
        Optional<UserDTO> user = userService.createUser(2 + email, login + 2, password);
        assertThat(user.isPresent()).isTrue();

        List<Role> newRolesList = List.of(Role.APP1_USER, Role.APP2_USER);
        UserDTO userDTO = UserDTO.builder()
                .roles(newRolesList)
                .build();

        assertThat(userService.updateUser(userDTO, user.get().getId())).isEqualTo(Optional.of(userDTO));
    }

    @Test
    @Order(5)
    public void shouldFindUserById() {
        Optional<UserDTO> user = userService.createUser(3 + email, login + 3, password);
        assertThat(user.isPresent()).isTrue();

        assertThat(userService.getUser(user.get().getId())).isPresent();
    }
}
