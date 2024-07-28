package xyz.s4i5.userservice.encoder;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.s4i5.userservice.config.EncoderConfig;

@SpringBootTest(
        classes = {
                EncoderConfig.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class PasswordEncoderTest {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    void encode() {
        var givenPassword = "somePassword";


        var encoded = passwordEncoder.encode(givenPassword);


        Assertions.assertThat(encoded)
                .isNotBlank()
                .isNotEqualTo(givenPassword);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void returnErrorWhenTryingToEncodeEmptyString(String pass) {
        Assertions.assertThatThrownBy(() -> passwordEncoder.encode(pass))
                .isInstanceOf(EncodePasswordException.class);
    }

    @Test
    void compareEqualPasswordAndHash() {
        var givenPassword = "pass";


        var actual = passwordEncoder.compareHashAndPassword(
                passwordEncoder.encode(givenPassword), givenPassword
        );


        Assertions.assertThat(actual)
                .isTrue();
    }

    @Test
    void compareNotEqualPasswordAndHash() {
        var actual = passwordEncoder.compareHashAndPassword(
                passwordEncoder.encode("pass1"), "pass2"
        );


        Assertions.assertThat(actual)
                .isFalse();
    }

}
