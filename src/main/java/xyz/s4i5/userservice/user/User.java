package xyz.s4i5.userservice.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Builder
@Document("users")
@AllArgsConstructor
public class User {
    @Id
    public String id;
    @Indexed(unique = true)
    private String login;
    @Indexed(unique = true)
    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    private String fullName;
    private String password;
    private List<Role> roles;
}
