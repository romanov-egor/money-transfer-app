package ru.romanov.mta.servlet.model;

/**
 * Model for transfer request
 *
 * @author Egor Romanov
 */
public class TransferModel {

    private long senderId;

    private long recipientId;

    private double transferAmount;

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(long recipientId) {
        this.recipientId = recipientId;
    }

    public double getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(double transferAmount) {
        this.transferAmount = transferAmount;
    }
}
