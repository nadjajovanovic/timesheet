package projekat.exception;

import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

public class MailException extends ApiException{
    public MailException(String message, HttpStatus s) {
        super(message,s);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
