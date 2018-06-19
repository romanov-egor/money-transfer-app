package ru.romanov.mta.persistence.exception;

import org.hibernate.Session;

/**
 * This exception should be thrown if unable to create {@link Session} or repository class unable to perform some
 * operation (create, get, update, delete, etc.)
 *
 * @author Egor Romanov
 */
public class ApplicationPersistenceException extends Exception {

    public ApplicationPersistenceException() {
    }

    public ApplicationPersistenceException(String message) {
        super(message);
    }

    public ApplicationPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationPersistenceException(Throwable cause) {
        super(cause);
    }
}
