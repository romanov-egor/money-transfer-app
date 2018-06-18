package ru.romanov.mtsa.servlet.model;

/**
 * Transfer presentation model to represent money transfer between accounts
 *
 * @author Egor Romanov
 */
public class TransferJson {

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
