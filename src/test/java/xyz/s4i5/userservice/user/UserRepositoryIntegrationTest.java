package xyz.s4i5.userservice.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
public class UserRepositoryIntegrationTest {
    private final String login = "tests_lover";
    private final String email = "example@gmail.com";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Container
    @ServiceConnection
    private final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @BeforeEach
    public void setup(){
        mongoTemplate.dropCollection(User.class);

        mongoTemplate.createCollection(User.class);
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("login", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps(User.class).ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
    }

    @Test
    public void isConnectionEstablished(){
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

    @Test
    public void shouldReturnUser(){

        userRepository.save(User.builder().email(email).login(login).build());

        var userForSearch = User.builder()
                .id(null)
                .login(login)
                .build();

        var userPage = userRepository.findAll(Example.of(userForSearch), PageRequest.of(0, 10));

        assertThat(userPage.isEmpty()).isFalse();

        userForSearch = User.builder()
                .id(null)
                .login(null)
                .email(email)
                .fullName(null)
                .build();

        userPage = userRepository.findAll(Example.of(userForSearch), PageRequest.of(0, 10));

        assertThat(userPage.isEmpty()).isFalse();
    }
}
