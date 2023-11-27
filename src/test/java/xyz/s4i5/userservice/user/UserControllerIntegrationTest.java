package xyz.s4i5.userservice.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import xyz.s4i5.userservice.user.dto.UserDTO;
import xyz.s4i5.userservice.user.requests.CreateUserRequest;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    private final String login = "tests_lover";
    private final String email = "example@gmail.com";
    private final String password = "password";
    private final String httpPath = "http://localhost:8080/api/v1";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    public void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest(email, login, password);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated());
    }

    @Test
    @Order(2)
    public void shouldFindNewUserByID() throws Exception {
        CreateUserRequest request = new CreateUserRequest(1 + email, login + 1, password);
        String requestJson = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated()).andReturn();

        String userId = JsonPath.read(response.getResponse().getContentAsString(), "$.user.id");

        mockMvc.perform(get(httpPath + "/users/" + userId)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void shouldDeleteUserById() throws Exception {
        CreateUserRequest request = new CreateUserRequest(2 + email, login + 2, password);
        String requestJson = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated()).andReturn();

        String userId = JsonPath.read(response.getResponse().getContentAsString(), "$.user.id");

        mockMvc.perform(delete(httpPath + "/users/" + userId)
        ).andExpect(status().isNoContent());
    }

    @Test
    @Order(4)
    public void shouldNotCreateUserWithExistingEmailOrLogin() throws Exception {
        CreateUserRequest request = new CreateUserRequest(3 + email, login, password);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest()).andReturn();

        request = new CreateUserRequest(email, login + 3, password);
        requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @Order(5)
    public void shouldUpdateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest(4 + email, login + 4, password);
        String requestJson = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post(httpPath + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isCreated()).andReturn();

        String userId = JsonPath.read(response.getResponse().getContentAsString(), "$.user.id");

        List<Role> newRolesList = List.of(Role.APP1_USER, Role.APP2_USER);

        UserDTO userDTO = UserDTO.builder()
                .roles(newRolesList)
                .build();
        requestJson = objectMapper.writeValueAsString(userDTO);

        JSONArray expectedRoles = new JSONArray();
        expectedRoles.appendElement("APP1_USER");
        expectedRoles.appendElement("APP2_USER");

        mockMvc.perform(patch(httpPath + "/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson)
        ).andExpect(status().isAccepted()).andExpect(
                (jsonPath("$.changed_fields.roles").value(expectedRoles)
                )).andReturn();
    }

    @Test
    @Order(6)
    public void shouldReturnListOfUsers() throws Exception {
        mockMvc.perform(get(httpPath + "/users")
        ).andExpect(status().isOk());
    }

    @Test
    @Order(7)
    public void shouldReturnUsersWithRole() throws Exception {
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
