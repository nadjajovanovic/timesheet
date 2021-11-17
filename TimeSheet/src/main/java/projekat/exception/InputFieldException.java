package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class InputFieldException extends ApiException {

        private HttpStatus httpStatus;

        public InputFieldException(String message, ErrorCode e) {
                super(message, e);
                this.httpStatus = HttpStatus.BAD_REQUEST;
        }
}
