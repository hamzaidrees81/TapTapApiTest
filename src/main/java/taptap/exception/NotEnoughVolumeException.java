package taptap.exception;

public class NotEnoughVolumeException extends UpgradeFailedException{
    public NotEnoughVolumeException(String message) {
        super(message);
    }

    public NotEnoughVolumeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughVolumeException(Throwable cause) {
        super(cause);
    }
}
