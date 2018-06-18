package ru.romanov.mtsa.servlet.model;

/**
 * Common error presentation model
 *
 * @author Egor Romanov
 */
public class ErrorJson {

    private int errorCode;

    private String message;

    public ErrorJson() {};

    public ErrorJson(int errorCode, String message) {
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
