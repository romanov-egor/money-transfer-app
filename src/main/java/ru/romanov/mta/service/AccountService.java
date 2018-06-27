package ru.romanov.mta.service;

import ru.romanov.mta.service.converter.ModelConverter;
import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.servlet.model.AccountModel;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class AccountService {

    private AccountRepository accountRepository = new AccountRepository();

    private AccountLockService accountLockService = AccountLockService.getInstance();

    /**
     * Gets Account by identifier as {@link AccountModel}
     * @param id Account identifier
     * @return
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public AccountModel getAccount(long id) throws NoSuchAccountException, ApplicationPersistenceException {
        AccountModel accountModel;
        ReentrantLock lock = accountLockService.getLock(id);
        lock.lock();
        try {
            accountModel = ModelConverter.toModel(accountRepository.get(id));
        } finally {
            lock.unlock();
        }
        return accountModel;
    }

    /**
     * Gets all Accounts as {@link List} of {@link AccountModel}
     * @return
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public List<AccountModel> getAccounts() throws ApplicationPersistenceException {
        return accountRepository.getAll().stream().map(ModelConverter::toModel).collect(Collectors.toList());
    }

    /**
     * Creates Account from {@link AccountModel}
     * @param accountModel Model with incoming parameters. {@link AccountModel#id} will be ignored
     * @return {@link AccountModel} that represents created {@link Account} (including it's identifier)
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public AccountModel createAccount(AccountModel accountModel) throws ApplicationPersistenceException {
        AccountModel createdAccount;
        ReentrantLock lock = accountLockService.getLock(accountModel.getId());
        lock.lock();
        try {
            createdAccount = ModelConverter.toModel(accountRepository.create(ModelConverter.toEntity(accountModel)));
        } finally {
            lock.unlock();
        }
        return createdAccount;
    }

    /**
     * Updates Account from {@link AccountModel}
     * @param accountModel Model with incoming parameters. {@link AccountModel#id} is used to find {@link Account}
     * to update
     * @return {@link AccountModel} that represents updated {@link Account}
     * @throws NoSuchAccountException if no Account with {@link AccountModel#id} present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public void updateAccount(AccountModel accountModel) throws NoSuchAccountException,
            ApplicationPersistenceException {
        ReentrantLock lock = accountLockService.getLock(accountModel.getId());
        lock.lock();
        try {
            accountRepository.update(ModelConverter.toEntity(accountModel));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deletes Account by identifier
     * @param accountId Account identifier
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public void deleteAccount(long accountId) throws NoSuchAccountException, ApplicationPersistenceException {
        ReentrantLock lock = accountLockService.getLock(accountId);
        lock.lock();
        try {
            accountRepository.delete(accountId);
        } finally {
            lock.unlock();
        }
    }
}
