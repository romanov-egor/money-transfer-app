package ru.romanov.mtsa.servlet.exception;

public class TransferJsonValidationException extends RuntimeException {

    public TransferJsonValidationException() {
    }

    public TransferJsonValidationException(String message) {
        super(message);
    }

    public TransferJsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferJsonValidationException(Throwable cause) {
        super(cause);
    }
}
