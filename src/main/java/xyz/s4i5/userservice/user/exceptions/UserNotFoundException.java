package xyz.s4i5.userservice.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
public class UserNotFoundException extends RuntimeException{
}
