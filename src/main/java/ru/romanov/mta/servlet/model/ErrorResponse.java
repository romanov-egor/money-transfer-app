package ru.romanov.mta.servlet.model;

/**
 * Common error model
 *
 * @author Egor Romanov
 */
public class ErrorResponse {

    private int errorCode;

    private String message;

    public ErrorResponse() {};

    public ErrorResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int status) {
        this.errorCode = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
