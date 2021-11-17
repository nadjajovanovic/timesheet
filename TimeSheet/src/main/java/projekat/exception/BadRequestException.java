package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class BadRequestException extends ApiException {

    public BadRequestException(String message, HttpStatus s) {
        super(message,s);
        this.errorCode = ErrorCode.BAD_REQUEST;
    }
}
