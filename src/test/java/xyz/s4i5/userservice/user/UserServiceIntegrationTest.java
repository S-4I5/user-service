package xyz.s4i5.userservice.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.s4i5.userservice.user.dto.UpdateUserDto;
import xyz.s4i5.userservice.user.exceptions.CannotCreateUserException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@Testcontainers
@SpringBootTest
public class UserServiceIntegrationTest {
    private final String login = "tests_lover";
    private final String email = "example@gmail.com";
    private final String password = "password";
    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");
    @Autowired
    private UserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup(){
        mongoTemplate.dropCollection(User.class);

        mongoTemplate.createCollection(User.class);
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("login", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }

    @Test
    @Rollback
    public void shouldSaveUser() {
        assertThat(userService.createUser(email, login, password)).isNotNull();
    }

    @Test
    @Rollback
    public void shouldNotSaveUserWithSameLoginAndEmail() {
        userService.createUser(email, login, password);

        assertThrows(CannotCreateUserException.class,
                () -> userService.createUser(1 + email, login, password));

        assertThrows(CannotCreateUserException.class,
                () -> userService.createUser(email, login + 1, password));
    }

    @Test
    @Rollback
    public void shouldDeleteUser() {
        var user = userService.createUser(email, login, password);

        assertThat(userService.deleteUser(user.getId())).isNotNull();
    }

    @Test
    @Rollback
    public void shouldUpdateUser() {
        var user = userService.createUser(email, login, password);

        var newRolesList = List.of(Role.APP1_USER, Role.APP2_USER);

        var updateUserDto = UpdateUserDto.builder()
                .roles(newRolesList)
                .build();

        assertThat(userService.updateUser(updateUserDto, user.getId()).getRoles()).isEqualTo(newRolesList);
    }

    @Test
    @Rollback
    public void shouldFindUserById() {
        var user = userService.createUser(email, login, password);

        assertThat(userService.getUser(user.getId())).isNotNull();
    }

    @Test
    @Rollback
    public void shouldReturnListOfUsers() {
        var user = userService.createUser(email, login, password);
        System.out.println(user);

        var userDtoList = userService.getUsers(user, List.of(), 0, 10);

        assertThat(userDtoList.isEmpty()).isFalse();
    }
}
