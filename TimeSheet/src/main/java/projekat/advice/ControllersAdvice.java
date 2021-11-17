package projekat.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projekat.controller.CategoryController;
import projekat.controller.TeamMemberController;
import projekat.enums.ErrorCode;
import projekat.exception.ApiException;
import projekat.exception.ErrorResponse;

import java.util.NoSuchElementException;

@ControllerAdvice
public class ControllersAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleApiException(NoSuchElementException e) {
        final var exception = new ErrorResponse(ErrorCode.NOT_FOUND.toString(),"Element with that id not exist in DB",HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        final var error = new ErrorResponse(e);
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(ApiException e) {
        final var error = new ApiException(e.getMessage(),e.getCause(),ErrorCode.INTERNAL_SERVER_ERROR);
        return handleApiException(error);
    }
}
