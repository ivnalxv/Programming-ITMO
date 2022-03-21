package info.kgeorgiy.ja.alekseev.walk;

public class WalkException extends Exception {
    WalkException(String message) {
        super(message);
    }

    WalkException(String message, Exception e) {
        super(message + (e == null ? "" : String.format("%n") + e.getMessage()));
    }
}
