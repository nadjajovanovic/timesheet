package projekat.exception;

import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

import java.time.ZonedDateTime;

public class BadRequestException extends ApiException{

    public BadRequestException() {
    }

    public BadRequestException(String message, Throwable throwable, HttpStatus httpStatus, ZonedDateTime timestamp, ErrorCode e) {
        super(message, throwable, httpStatus, e);
    }

    public BadRequestException(String message, HttpStatus httpStatus, ErrorCode e) {
        super(message, httpStatus, e);
    }

    public BadRequestException(String s) {
        super(s);
    }
}
