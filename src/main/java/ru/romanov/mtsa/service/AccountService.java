package ru.romanov.mtsa.service;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;

import java.util.List;

public interface AccountService {

    public Account createAccount(String holderName, double balance);

    public void transferMoney(long senderId, long recipientId, double transferAmount);

    public Account getAccount(long id);

    public List<Account> getAccounts();

    Account updateAccount(long accountId, String holderName, double balance);

    void deleteAccount(long accountId);
}
