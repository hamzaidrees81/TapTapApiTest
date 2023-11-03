package taptap.exception;

public class TapTapClientException extends RuntimeException {

    public TapTapClientException(String message) {
        super(message);
    }

    public TapTapClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public TapTapClientException(Throwable cause) {
        super(cause);
    }
}
