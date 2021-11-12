package projekat.exception;

public class EmptyInputException extends RuntimeException{
    public EmptyInputException() {
    }

    public EmptyInputException(String message) {
        super(message);
    }

    public EmptyInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyInputException(Throwable cause) {
        super(cause);
    }

    public EmptyInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
