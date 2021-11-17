package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class BadRequestException extends ApiException{

    private HttpStatus httpStatus;

    public BadRequestException(String message, ErrorCode e) {
        super(message,e);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
