package projekat.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse implements Serializable {
    private  String errorCode;
    private  String errorMessage;
    private int statusCode;

    @Setter(AccessLevel.NONE)
    private String time;

    public ErrorResponse(ApiException apiException) {
        this.errorCode = apiException.getErrorCode().toString();
        this.errorMessage =  apiException.getMessage().isEmpty() ? null : apiException.getMessage() ;
        this.statusCode = apiException.getHttpStatus().value();
        this.time = ZonedDateTime.now(ZoneId.of("Z")).toString();
    }

    public ErrorResponse(String errorCode, String errorMessage, int statusCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.time = ZonedDateTime.now(ZoneId.of("Z")).toString();
    }
}
