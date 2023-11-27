package xyz.s4i5.userservice.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    public void isConnectionEstablished(){
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

    private final String login = "tests_lover";
    private final String email = "example@gmail.com";

    @Test
    @Order(2)
    public void shouldReturnUser(){

        userRepository.save(User.builder().email(email).login(login).build());

        User ex = User.builder()
                .id(null)
                .login(login)
                .build();

        Example<User> example = Example.of(ex);

        Page<User> userPage = userRepository.findAll(example, PageRequest.of(0, 10));

        assertThat(userPage.isEmpty()).isFalse();

        ex = User.builder()
                .id(null)
                .email(email)
                .build();

        example = Example.of(ex);

        userPage = userRepository.findAll(example, PageRequest.of(0, 10));

        assertThat(userPage.isEmpty()).isFalse();
    }
}
