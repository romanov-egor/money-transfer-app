package ru.romanov.mta.servlet.exception;

import ru.romanov.mta.servlet.model.TransferModel;

/**
 * This exception should be thrown if received {@link TransferModel} has incorrect parameters
 *
 * @author Egor Romanov
 */
public class TransferModelValidationException extends Exception {

    public TransferModelValidationException() {
    }

    public TransferModelValidationException(String message) {
        super(message);
    }

    public TransferModelValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransferModelValidationException(Throwable cause) {
        super(cause);
    }
}
