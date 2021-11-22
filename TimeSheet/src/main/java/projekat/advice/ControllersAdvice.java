package projekat.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projekat.enums.ErrorCode;
import projekat.exception.ApiException;
import projekat.exception.ErrorResponse;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ControllersAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleApiException(NoSuchElementException e) {
        final var exception = new ErrorResponse(ErrorCode.NOT_FOUND.toString(),"Element with that id not exist in DB",HttpStatus.NOT_FOUND.value());
        log.error("API error! errorCode: {}", exception.getErrorCode(), e);
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        final var error = new ErrorResponse(e);
        log.error("API error! errorCode: {}", e.getErrorCode(), e);
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(ApiException e) {
        final var error = new ApiException(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR,e.getCause());
        log.error("API error! errorCode: {}", e.getErrorCode(), e);
        return handleApiException(error);
    }
}
