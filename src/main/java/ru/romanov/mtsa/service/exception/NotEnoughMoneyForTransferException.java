package ru.romanov.mtsa.service.exception;

/**
 * This exception should be thrown if sender's account balance is less than transfer amount. This is a business logic
 * exception
 *
 * @author Egor Romanov
 */
public class NotEnoughMoneyForTransferException extends RuntimeException {

    public NotEnoughMoneyForTransferException() {
    }

    public NotEnoughMoneyForTransferException(String message) {
        super(message);
    }

    public NotEnoughMoneyForTransferException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughMoneyForTransferException(Throwable cause) {
        super(cause);
    }
}
