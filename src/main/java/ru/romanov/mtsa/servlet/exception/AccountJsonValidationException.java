package ru.romanov.mtsa.servlet.exception;

public class AccountJsonValidationException extends RuntimeException {

    public AccountJsonValidationException() {
    }

    public AccountJsonValidationException(String message) {
        super(message);
    }

    public AccountJsonValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountJsonValidationException(Throwable cause) {
        super(cause);
    }
}
