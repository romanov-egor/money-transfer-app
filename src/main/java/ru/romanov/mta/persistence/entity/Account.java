package ru.romanov.mta.persistence.entity;

import javax.persistence.*;


/**
 * Account JPA entity
 *
 * @author Egor Romanov
 */
@Entity
@Table
public class Account {

    @Id
    @Column
    @GeneratedValue
    private long id;

    @Column
    private String holderName;

    @Column
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
}
