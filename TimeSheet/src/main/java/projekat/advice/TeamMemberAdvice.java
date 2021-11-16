package projekat.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import projekat.controller.TeamMemberController;
import projekat.exception.ApiException;
import projekat.exception.EmptyInputException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class TeamMemberAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<String> internalServerError(HttpServerErrorException.InternalServerError internalServerError){
        return new ResponseEntity<>("Something went wrong from our side", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> noSuchElementException(NoSuchElementException noSuchElementException){
        return new ResponseEntity<>("No element with that id in DB", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyInputException.class)
    public ResponseEntity<Object> emptyInputException(EmptyInputException e){
        final var exception = new ApiException(e.getMessage(),e,HttpStatus.BAD_REQUEST, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<>("Wrong http request, please change", HttpStatus.BAD_REQUEST);

    }
}
