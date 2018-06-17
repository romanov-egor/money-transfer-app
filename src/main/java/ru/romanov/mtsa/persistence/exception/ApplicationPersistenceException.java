package ru.romanov.mtsa.persistence.exception;

public class ApplicationPersistenceException extends  RuntimeException {

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
