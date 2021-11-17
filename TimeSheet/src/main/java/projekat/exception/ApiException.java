package projekat.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

import java.time.ZonedDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ApiException extends RuntimeException {
    private  String message;
    private  Throwable throwable;
    private  HttpStatus httpStatus;
    private ZonedDateTime timestamp;
    private ErrorCode errorCode;

    public ApiException(String message, Throwable throwable, HttpStatus httpStatus, ZonedDateTime timestamp,ErrorCode e) {
        this.message = message;
        this.throwable = throwable;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
        this.errorCode = e;
    }
    public ApiException(String message,  HttpStatus httpStatus, ZonedDateTime timestamp,ErrorCode e) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.timestamp = timestamp;
        this.errorCode = e;
    }

    public ApiException(String s) {
        this.message = s;
    }
}
