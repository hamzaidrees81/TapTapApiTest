package taptap.exception;

public class TransctionFailedException extends TapTapClientException{
    public TransctionFailedException(String message) {
        super(message);
    }

    public TransctionFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransctionFailedException(Throwable cause) {
        super(cause);
    }
}
