package xyz.s4i5.userservice.encoder;

import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String encode(String input) {
        return input.toLowerCase() + "2";
    }
}
