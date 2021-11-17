package projekat.exception;

import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

import java.time.ZonedDateTime;

public class BadRequestException extends ApiException{

    public BadRequestException() {
    }

    public BadRequestException(String message, Throwable throwable, HttpStatus httpStatus, ZonedDateTime timestamp, ErrorCode e) {
        super(message, throwable, httpStatus, timestamp, e);
    }

    public BadRequestException(String message, HttpStatus httpStatus, ZonedDateTime timestamp, ErrorCode e) {
        super(message, httpStatus, timestamp, e);
    }

    public BadRequestException(String s) {
        super(s);
    }
}
