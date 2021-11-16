package projekat.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projekat.controller.TeamMemberController;
import projekat.exception.ApiException;
import projekat.exception.ObjectNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice(assignableTypes = TeamMemberController.class)
public class ControllersAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<String> internalServerError(HttpServerErrorException.InternalServerError internalServerError) {
        return new ResponseEntity<>("Something went wrong from our side", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> noSuchElementException(NoSuchElementException e) {
        final var exception = new ApiException("No element with that id in DB", e.getCause(), HttpStatus.NOT_FOUND, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Object> objectNotFoundException(ObjectNotFoundException e) {
        final var exception = new ApiException(e.getMessage(), e, HttpStatus.NOT_FOUND, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final var exception = new ApiException("Wrong http request, please change", ex, HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final var exception = new ApiException(String.format("Property %s is null or wrong format", ex.getFieldError().getField()), ex, HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception.toString(), HttpStatus.BAD_REQUEST);
    }
}
