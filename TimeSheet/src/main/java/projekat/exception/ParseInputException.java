package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class ParseInputException extends ApiException {

    private HttpStatus httpStatus;

    public ParseInputException(String message,HttpStatus s) {
        super(message, s);
        this.errorCode = ErrorCode.INVALID_DATE_FORMAT;
    }
}
