package xyz.s4i5.userservice.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Can't update User")
public class CannotUpdateUserException extends RuntimeException {
}
