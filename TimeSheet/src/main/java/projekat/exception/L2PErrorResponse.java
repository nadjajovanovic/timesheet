package projekat.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import projekat.enums.ErrorCode;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class L2PErrorResponse implements Serializable {
    private  String errorCode;
    private  String errorMessage;
    private int statusCode;

    @Setter(AccessLevel.NONE)
    private String time;

    public L2PErrorResponse(ApiException apiException) {
        this.errorCode = apiException.getErrorCode().toString();
        this.errorMessage =  apiException.getMessage().isEmpty() ? null : apiException.getMessage() ;
        this.statusCode = apiException.getHttpStatus().value();
        this.time = apiException.getTimestamp().toString();
    }

    public L2PErrorResponse(String errorCode, String errorMessage, int statusCode, String time) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.time = time;
    }
}
