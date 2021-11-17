package projekat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

@Getter
@Setter
public class InputFieldException extends ApiException {

        public InputFieldException(String message, HttpStatus s) {
                super(message, s);
                this.errorCode = ErrorCode.NOT_FOUND;
        }
}
