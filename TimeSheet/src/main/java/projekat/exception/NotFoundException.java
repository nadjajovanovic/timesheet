package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class NotFoundException extends ApiException {

    private HttpStatus httpStatus;

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
        this.httpStatus = HttpStatus.NOT_FOUND;
    }
}
