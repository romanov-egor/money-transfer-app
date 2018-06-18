package ru.romanov.mtsa.service.exception;

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
