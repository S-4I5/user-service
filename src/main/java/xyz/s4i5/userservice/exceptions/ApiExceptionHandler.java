package xyz.s4i5.userservice.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class ApiExceptionHandler {
    private final int MAX_TRACE_LENGTH = 20;

    private final MessageSource messageSource;

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDto handleDataIntegrityViolationException(DataIntegrityViolationException exception, Locale locale) {
        return createErrorDto(exception, locale, HttpStatus.BAD_REQUEST, "api.user.create.uniqueFieldDuplicate",
                List.of(exception.getRootCause().getMessage().split("Подробности:")[1]).toArray());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDto handleException(Exception exception, Locale locale) {
        return createErrorDto(exception, locale, HttpStatus.INTERNAL_SERVER_ERROR, "exception.unexpected", null);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserApiException.class)
    public ErrorDto handleUserApiException(UserApiException exception, Locale locale) {
        return createErrorDto(exception, locale, HttpStatus.BAD_REQUEST, exception.getMessageCode(), exception.getArgs().toArray());
    }

    private ErrorDto createErrorDto(
            Exception exception, Locale locale, HttpStatus httpStatus,
            String messageCode, Object[] args
    ) {
        String errorMessage = this.messageSource.getMessage(messageCode, args, locale);
        return createErrorDto(exception, errorMessage, messageCode, httpStatus.value());
    }

    private ErrorDto createErrorDto(Exception exception, String errorMessage, String messageCode, int statusCode) {
        return ErrorDto.builder()
                .messageCode(messageCode)
                .message(errorMessage)
                .trace(mapStackTrace(exception.getStackTrace()))
                .status(statusCode)
                .build();
    }

    private List<String> mapStackTrace(StackTraceElement[] stackTrace) {
        return Arrays.stream(stackTrace)
                .limit(MAX_TRACE_LENGTH)
                .map(StackTraceElement::toString)
                .toList();
    }
}
