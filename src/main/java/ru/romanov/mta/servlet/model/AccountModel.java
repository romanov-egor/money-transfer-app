package ru.romanov.mta.servlet.model;

/**
 * @author Egor Romanov
 */
public class AccountModel {

    private long id;

    private String holderName;

    private double balance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccountModel other = (AccountModel) obj;
        if (id != other.id) {
            return false;
        }
        if (null == holderName) {
            if (null != other.holderName) {
                return false;
            }
        } else {
            if (!holderName.equals(other.holderName)) {
                return false;
            }
        }
        if (balance != other.balance) {
            return false;
        }

        return true;
    }
}
