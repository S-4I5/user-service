package xyz.s4i5.userservice.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.s4i5.userservice.user.dto.CreateUserDto;
import xyz.s4i5.userservice.user.dto.UserDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    private final String login = "tests_lover";
    private final String email = "example@gmail.com";
    private final String password = "password";
    private final String httpPath = "http://localhost:8080/api/v1";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Container
    @ServiceConnection
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @BeforeEach
    public void setup() {
        mongoTemplate.dropCollection(User.class);

        mongoTemplate.createCollection(User.class);
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("login", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }

    private String createUserWithDefaultRecords() throws Exception {
        var request = new CreateUserDto(email, login, password);
        var requestJson = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andReturn();

        System.out.println(JsonPath.read(response.getResponse().getContentAsString(), "$.id").toString());

        return JsonPath.read(response.getResponse().getContentAsString(), "$.id");
    }

    @Test
    public void shouldCreateUser() throws Exception {
        var request = new CreateUserDto(email, login, password);
        var requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated());
    }

    @Test
    public void shouldFindNewUserByID() throws Exception {
        var userId = createUserWithDefaultRecords();

        mockMvc.perform(get(httpPath + "/users/" + userId)
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUserById() throws Exception {
        var userId = createUserWithDefaultRecords();

        mockMvc.perform(delete(httpPath + "/users/" + userId)
        ).andExpect(status().isAccepted());
    }

    @Test
    public void shouldNotCreateUserWithExistingEmailOrLogin() throws Exception {
        createUserWithDefaultRecords();

        var request = new CreateUserDto("other_email@gmail.com", login, password);
        var requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest()).andReturn();

        request = new CreateUserDto(email, "other_login", password);
        requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        var userId = createUserWithDefaultRecords();

        var newRolesList = List.of(Role.APP1_USER, Role.APP2_USER);

        var userDto = UserDto.builder()
                .roles(newRolesList)
                .build();
        var requestJson = objectMapper.writeValueAsString(userDto);

        JSONArray expectedRoles = new JSONArray();
        expectedRoles.appendElement("APP1_USER");
        expectedRoles.appendElement("APP2_USER");

        mockMvc.perform(put(httpPath + "/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isAccepted()).andExpect(
                (jsonPath("$.roles").value(expectedRoles)
                )).andReturn();
    }

    @Test
    public void shouldReturnListOfUsers() throws Exception {
        createUserWithDefaultRecords();

        mockMvc.perform(get(httpPath + "/users")
        ).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnUsersWithRole() throws Exception {
        var userId = createUserWithDefaultRecords();

        var newRolesList = List.of(Role.APP1_USER, Role.APP2_USER);

        var userToFindDto = UserDto.builder()
                .roles(newRolesList)
                .build();
        var requestJson = objectMapper.writeValueAsString(userToFindDto);

        JSONArray expectedRoles = new JSONArray();
        expectedRoles.appendElement("APP1_USER");
        expectedRoles.appendElement("APP2_USER");

        mockMvc.perform(put(httpPath + "/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isAccepted()).andExpect(
                (jsonPath("$.roles").value(expectedRoles)
                )).andReturn();

        mockMvc.perform(get(httpPath + "/users")
                .requestAttr("roles", String.valueOf(List.of(Role.APP1_USER)))
        ).andExpect(status().isOk());

        mockMvc.perform(get(httpPath + "/users")
                .requestAttr("roles", String.valueOf(List.of(Role.APP2_USER)))
        ).andExpect(status().isOk());

        mockMvc.perform(get(httpPath + "/users")
                .requestAttr("roles", String.valueOf(List.of(Role.APP1_USER, Role.APP2_USER)))
        ).andExpect(status().isOk());
    }

}
