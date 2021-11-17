package projekat.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

import java.time.ZoneId;
import java.time.ZonedDateTime;
@Getter
@Setter
@NoArgsConstructor
public class ApiException extends RuntimeException {
    protected  String message;
    protected  Throwable throwable;
    protected  HttpStatus httpStatus;
    protected ZonedDateTime timestamp;
    protected ErrorCode errorCode;

    public ApiException(String message,HttpStatus s ,Throwable throwable) {
        this.message = message;
        this.httpStatus = s;
        this.throwable = throwable;
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    }
    public ApiException(String message,HttpStatus s) {
        this.httpStatus = s;
        this.message = message;
        this.timestamp = ZonedDateTime.now(ZoneId.of("Z"));
    }
}
