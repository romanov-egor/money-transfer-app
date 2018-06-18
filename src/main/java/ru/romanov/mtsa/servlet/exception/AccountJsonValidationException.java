package ru.romanov.mtsa.servlet.exception;

import ru.romanov.mtsa.servlet.model.AccountJson;

/**
 * This exception should be thrown if received {@link AccountJson} model has incorrect parameters
 *
 * @author Egor Romanov
 */
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
