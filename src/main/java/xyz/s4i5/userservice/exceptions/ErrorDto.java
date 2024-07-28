package xyz.s4i5.userservice.exceptions;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorDto {
    private int status;
    private String message;
    private String messageCode;
    private List<String> trace;
}
