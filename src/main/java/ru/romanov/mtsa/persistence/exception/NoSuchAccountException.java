package ru.romanov.mtsa.persistence.exception;

/**
 * This exception should be thrown if there is no Account with given identifier in database
 *
 * @author Egor Romanov
 */
public class NoSuchAccountException extends RuntimeException {

    public NoSuchAccountException() {
    }

    public NoSuchAccountException(String message) {
        super(message);
    }

    public NoSuchAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchAccountException(Throwable cause) {
        super(cause);
    }
}
