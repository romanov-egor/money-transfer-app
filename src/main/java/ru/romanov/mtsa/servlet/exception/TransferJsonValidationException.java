package ru.romanov.mtsa.servlet.exception;

import ru.romanov.mtsa.servlet.model.TransferJson;

/**
 * This exception should be thrown if received {@link TransferJson} model has incorrect parameters
 *
 * @author Egor Romanov
 */
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
