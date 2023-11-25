package xyz.s4i5.userservice.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataMongoTest
public class UserRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");

    @Autowired
    private UserRepository userRepository;

    @Test
    public void isConnectionEstablished(){
        assertThat(mongoDBContainer.isCreated()).isTrue();
        assertThat(mongoDBContainer.isRunning()).isTrue();
    }

    @BeforeEach
    public void setup(){
        userRepository.save(User.builder().login("SUS").build());
    }

    @Test
    public void shouldReturnUserByLogin(){
        Optional<User> user = userRepository.findByLogin("SUS");

        assertThat(user.isPresent()).isTrue();
    }
}
