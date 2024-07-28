package xyz.s4i5.userservice.encoder;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

//Just something for avoid adding Spring Security BCryptPasswordEncoder for now
@RequiredArgsConstructor
public class PasswordEncoder {
    private final String secret;

    @SneakyThrows
    public String encode(String input) {
        if (StringUtils.isBlank(input)) {
            throw new EncodePasswordException(input);
        }

        return verySecretAndEfficientEncodingAlgorithm(input);
    }

    public boolean compareHashAndPassword(String hashedPassword, String password) {
        return hashedPassword.equals(verySecretAndEfficientEncodingAlgorithm(password));
    }

    private String verySecretAndEfficientEncodingAlgorithm(String input){
        return StringUtils.reverse(input) + secret + input;
    }
}
