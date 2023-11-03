package taptap.exception;

public class UpgradeFailedException extends TapTapClientException{
    public UpgradeFailedException(String message) {
        super(message);
    }

    public UpgradeFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpgradeFailedException(Throwable cause) {
        super(cause);
    }
}
