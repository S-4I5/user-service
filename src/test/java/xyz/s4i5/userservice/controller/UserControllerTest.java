package xyz.s4i5.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.s4i5.userservice.model.dto.user.CreateUserDto;
import xyz.s4i5.userservice.model.dto.user.UpdateUserDto;
import xyz.s4i5.userservice.model.entity.role.Role;
import xyz.s4i5.userservice.model.entity.role.RoleName;
import xyz.s4i5.userservice.model.entity.user.User;
import xyz.s4i5.userservice.repository.RoleRepository;
import xyz.s4i5.userservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.TestUtil.readJsonAsString;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerTest {
    private final String BASE_FOLDER = "UserControllerTest/";

    @Container
    @ServiceConnection
    private final static GenericContainer<?> postgreSQLContainer = new PostgreSQLContainer("postgres:16")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "user")
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "password");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void createUser() {
        var givenDto = CreateUserDto.builder()
                .email("example@gmail.com")
                .login("tests_lover")
                .fullName("fullName")
                .password("password")
                .roles(List.of(RoleName.APP1_USER))
                .build();
        var given = objectMapper.writeValueAsString(givenDto);


        var actual = mockMvc.perform(post(UserController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(given)
        );


        var expected = readJson("/response/createUser");
        actual
                .andExpect(status().isCreated())
                .andExpect(content().json(expected));
        Assertions.assertThat(userRepository.findAll().get(0))
                .hasFieldOrPropertyWithValue("email","example@gmail.com")
                .hasFieldOrPropertyWithValue("login", "tests_lover")
                .hasFieldOrPropertyWithValue("fullName", "fullName")
                .hasNoNullFieldsOrProperties();
        Assertions.assertThat(roleRepository.findAll().get(0))
                .hasFieldOrPropertyWithValue("roleName", RoleName.APP1_USER);
    }

    @Test
    @SneakyThrows
    void return400WhenCreateUserWithExistingEmail() {
        userRepository.save(User.builder()
                .email("same_email@gmail.com")
                .build());

        var givenDto = CreateUserDto.builder()
                .email("same_email@gmail.com")
                .login("tests_lover")
                .password("password")
                .build();
        var given = objectMapper.writeValueAsString(givenDto);


        var actual = mockMvc.perform(post(UserController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(given)
        );


        var expected = readJson("/response/return400WhenCreateUserWithExistingEmail");
        actual
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    @Test
    @SneakyThrows
    void return400WhenCreateUserWithExistingLogin() {
        userRepository.save(User.builder()
                .login("same_login")
                .build()
        );

        var givenDto = CreateUserDto.builder()
                .email("example@gmail.com")
                .login("same_login")
                .password("password")
                .build();
        var given = objectMapper.writeValueAsString(givenDto);


        var actual = mockMvc.perform(post(UserController.ROOT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(given)
        );


        var expected = readJson("/response/return400WhenCreateUserWithExistingLogin");
        actual
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    @Test
    @SneakyThrows
    void getUser() {
        var givenId = userRepository.save(User.builder()
                .login("login")
                .email("example@gmail.com")
                .password("XD")
                .build()
        ).getId();


        var actual = mockMvc.perform(
                get(
                        UserController.ROOT_URI + UserController.GET_URI,
                        givenId
                )
        );


        var expected = readJson("/response/getUser");
        actual
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @Test
    @SneakyThrows
    void return400WhenGetNonExistingUser() {
        var actual = mockMvc.perform(
                get(
                        UserController.ROOT_URI + UserController.GET_URI,
                        "11111111-1111-1111-1111-111111111111"
                )
        );


        var expected = readJson("/response/return400WhenGetNonExistingUser");
        actual
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        var givenUser = userRepository.save(User.builder()
                .login("login")
                .email("example@gmail.com")
                .password("XD")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_ADMIN)
                .user(givenUser)
                .build()
        );


        var actual = mockMvc.perform(
                delete(
                        UserController.ROOT_URI + UserController.DELETE_URI,
                        givenUser.getId()
                )
        );


        actual
                .andExpect(status().isNoContent());
        Assertions.assertThat(userRepository.findAll())
                .isEmpty();
        Assertions.assertThat(roleRepository.findAll())
                .isEmpty();
    }

    @Test
    @SneakyThrows
    void return400WhenDeleteNonExistingUser() {
        var actual = mockMvc.perform(
                delete(
                        UserController.ROOT_URI + UserController.DELETE_URI,
                        "11111111-1111-1111-1111-111111111111"
                )
        );


        var expected = readJson("/response/return400WhenDeleteNonExistingUser");
        actual
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    @Test
    @SneakyThrows
    void updateUser() {
        var givenUser = userRepository.save(User.builder()
                .login("oldLogin")
                .fullName("oldFullName")
                .email("oldemail@gmail.com")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_ADMIN)
                .user(givenUser)
                .build()
        );

        var givenDto = UpdateUserDto.builder()
                .login("newLogin")
                .fullName("newFullName")
                .roles(List.of(RoleName.APP2_USER))
                .email("newemail@gmail.com")
                .build();
        var given = objectMapper.writeValueAsString(givenDto);


        var actual = mockMvc.perform(
                patch(
                        UserController.ROOT_URI + UserController.UPDATE_URI,
                        givenUser.getId()
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(given)
        );


        var expected = readJson("/response/updateUser");
        actual
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
        Assertions.assertThat(userRepository.findAll().get(0))
                .hasFieldOrPropertyWithValue("email","newemail@gmail.com")
                .hasFieldOrPropertyWithValue("login", "newLogin")
                .hasFieldOrPropertyWithValue("fullName", "newFullName");
        Assertions.assertThat(roleRepository.findAll().get(0))
                .hasFieldOrPropertyWithValue("roleName", RoleName.APP2_USER);
    }

    @ParameterizedTest
    @MethodSource("provideArgsForReturn400WhenUpdateUserSettingExistingUniqueFields")
    @SneakyThrows
    void return400WhenUpdateUserSettingExistingUniqueFields(
            String login, String email, String fileName
    ) {
        userRepository.save(User.builder()
                .login("alreadyExists")
                .email("already.exists@gmail.com")
                .build()
        );

        var givenId = userRepository.save(User.builder()
                .login("oldLogin")
                .fullName("oldFullName")
                .email("oldemail@gmail.com")
                .build()
        ).getId();

        var givenDto = UpdateUserDto.builder()
                .login(login)
                .email(email)
                .build();
        var given = objectMapper.writeValueAsString(givenDto);


        var actual = mockMvc.perform(
                patch(
                        UserController.ROOT_URI + UserController.UPDATE_URI,
                        givenId
                )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(given)
        );


        var expected = readJson("/response/" + fileName);
        actual
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expected));
    }

    static Stream<Arguments> provideArgsForReturn400WhenUpdateUserSettingExistingUniqueFields() {
        return Stream.of(
                Arguments.of("alreadyExists", "newemail@gmail.com", "return400WhenUpdateUserSettingExistingLogin"),
                Arguments.of("newLogin", "already.exists@gmail.com", "return400WhenUpdateUserSettingExistingEmail")
        );
    }

    @Test
    @SneakyThrows
    void searchWithMultipleRoles() {
        var givenUser1 = userRepository.save(User.builder()
                .login("shouldFind")
                .fullName("shouldFind")
                .email("shouldFind@gmail.com")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_USER)
                .user(givenUser1)
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP2_USER)
                .user(givenUser1)
                .build()
        );

        var givenUser2 = userRepository.save(User.builder()
                .login("no")
                .fullName("no")
                .email("no@gmail.com")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_USER)
                .user(givenUser2)
                .build()
        );
        var given = readJson("/request/search/searchWithMultipleRoles");


        var actual = mockMvc.perform(
                post(UserController.ROOT_URI + UserController.SEARCH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(given)
        );


        var expected = readJson("/response/search/searchWithMultipleRoles");
        actual
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    @ParameterizedTest
    @MethodSource("provideArgsForSearch")
    @SneakyThrows
    void search(String fileName) {
        var givenUser1 = userRepository.save(User.builder()
                .login("shouldFind")
                .fullName("shouldFind")
                .email("shouldFind@gmail.com")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_USER)
                .user(givenUser1)
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP2_USER)
                .user(givenUser1)
                .build()
        );

        var givenUser2 = userRepository.save(User.builder()
                .login("no")
                .fullName("no")
                .email("no@gmail.com")
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP1_ADMIN)
                .user(givenUser2)
                .build()
        );
        roleRepository.save(Role.builder()
                .roleName(RoleName.APP2_ADMIN)
                .user(givenUser2)
                .build()
        );

        var given = readJson("/request/search/" + fileName);


        var actual = mockMvc.perform(
                post(UserController.ROOT_URI + UserController.SEARCH_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(given)
        );


        var expected = readJson("/response/search/" + fileName);
        actual
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    static Stream<Arguments> provideArgsForSearch() {
        return Stream.of(
                Arguments.of("searchUsersByEmail"),
                Arguments.of("searchUsersByFullName"),
                Arguments.of("searchUsersByLogin"),
                Arguments.of("searchUsersByRoles")
        );
    }

    private String readJson(String filename) throws Exception {
        return readJsonAsString(BASE_FOLDER + filename);
    }
}
