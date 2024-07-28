package xyz.s4i5.userservice.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class UserApiException extends RuntimeException  {
    private final String messageCode;
    private final List<String> args;

    public UserApiException(String messageCode, List<String> args) {
        this.messageCode = messageCode;
        this.args = args;
    }
}
