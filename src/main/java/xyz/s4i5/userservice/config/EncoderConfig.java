package xyz.s4i5.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.s4i5.userservice.encoder.PasswordEncoder;

@Configuration
public class EncoderConfig {
    @Value("${secret}")
    private String secret;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder(secret);
    }
}
