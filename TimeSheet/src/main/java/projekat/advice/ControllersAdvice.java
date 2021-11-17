package projekat.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projekat.controller.TeamMemberController;
import projekat.enums.ErrorCode;
import projekat.exception.ApiException;
import projekat.exception.L2PErrorResponse;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice(assignableTypes = TeamMemberController.class)
public class ControllersAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleApiException(NoSuchElementException e) {
        final var exception = new L2PErrorResponse(ErrorCode.NOT_FOUND.toString(),"Element with that id not exist in DB",HttpStatus.NOT_FOUND.value(),ZonedDateTime.now(ZoneId.of("Z")).toString());
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<L2PErrorResponse> handleApiException(ApiException e) {
        final var error = new L2PErrorResponse(e);
        return new ResponseEntity<>(error, e.getHttpStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<L2PErrorResponse> handleRuntimeException(ApiException e) {
        final var error = new ApiException(e.getMessage(),e.getCause(),HttpStatus.INTERNAL_SERVER_ERROR, ZonedDateTime.now(ZoneId.of("Z")),ErrorCode.INTERNAL_SERVER_ERROR);
        return handleApiException(error);
    }
}
