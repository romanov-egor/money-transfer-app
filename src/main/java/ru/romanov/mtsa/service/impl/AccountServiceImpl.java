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
    public void createAccount(String holderName, double balance) {
        accountRepository.create(holderName, balance);
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
}
