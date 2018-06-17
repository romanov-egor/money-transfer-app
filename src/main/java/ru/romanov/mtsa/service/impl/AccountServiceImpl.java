package ru.romanov.mtsa.service.impl;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.service.AccountService;

import java.util.List;

public class AccountServiceImpl implements AccountService {

    private static volatile AccountService instance;

    private AccountRepository accountRepository = new AccountRepository();

    public static AccountService getInstance() {
        if (instance == null) {
            synchronized (AccountServiceImpl.class) {
                if (instance == null) {
                    instance = new AccountServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public Account createAccount(String holderName, double balance) {
        long id = accountRepository.create(holderName, balance);
        return accountRepository.get(id);
    }

    @Override
    public void transferMoney(long senderId, long recipientId, double transferAmount) {

    }

    @Override
    public Account getAccount(long id) {
        return accountRepository.get(id);
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.getAll();
    }

    @Override
    public Account updateAccount(long accountId, String holderName, double balance) {
        Account account = new Account();
        account.setId(accountId);
        account.setHolderName(holderName);
        account.setBalance(balance);

        accountRepository.update(account);

        return account;
    }

    @Override
    public void deleteAccount(long accountId) {
        accountRepository.delete(accountId);
    }
}
