package it.alessandrocalista.customenchants.hook;

public class HookException extends RuntimeException {
    public HookException(String message) {
        super(message);
    }

    public HookException(String message, Throwable cause) {
        super(message, cause);
    }

    public HookException(Throwable cause) {
        super(cause);
    }
}
