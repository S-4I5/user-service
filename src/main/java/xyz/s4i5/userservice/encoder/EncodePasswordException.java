package xyz.s4i5.userservice.encoder;

public class EncodePasswordException extends Exception {
    private static final String MESSAGE = "Cannot build hash out of %s string";
    public EncodePasswordException(String string) {
        super(String.format(MESSAGE, string));
    }
}
