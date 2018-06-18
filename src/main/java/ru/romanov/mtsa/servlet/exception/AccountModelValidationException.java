package ru.romanov.mtsa.servlet.exception;

import ru.romanov.mtsa.servlet.model.AccountModel;

/**
 * This exception should be thrown if received {@link AccountModel} has incorrect parameters
 *
 * @author Egor Romanov
 */
public class AccountModelValidationException extends Exception {

    public AccountModelValidationException() {
    }

    public AccountModelValidationException(String message) {
        super(message);
    }

    public AccountModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountModelValidationException(Throwable cause) {
        super(cause);
    }
}
