package ru.romanov.mtsa.service.impl;

import ru.romanov.mtsa.converter.ModelConverter;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

import java.util.List;
import java.util.stream.Collectors;

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
    public AccountJsonModel getAccount(long id) throws NoSuchAccountException, ApplicationPersistenceException {
        return ModelConverter.toJsonModel(accountRepository.get(id));
    }

    @Override
    public List<AccountJsonModel> getAccounts() throws ApplicationPersistenceException {
        return accountRepository.getAll().stream().map(ModelConverter::toJsonModel).collect(Collectors.toList());
    }

    @Override
    public AccountJsonModel createAccount(AccountJsonModel accountJsonModel) throws ApplicationPersistenceException {
        Account createdAccount = accountRepository.create(ModelConverter.toPersistenceModel(accountJsonModel));
        return ModelConverter.toJsonModel(createdAccount);
    }

    @Override
    public void updateAccount(AccountJsonModel accountJsonModel) throws NoSuchAccountException,
            ApplicationPersistenceException {
        accountRepository.update(ModelConverter.toPersistenceModel(accountJsonModel));
    }

    @Override
    public void deleteAccount(long accountId) throws NoSuchAccountException, ApplicationPersistenceException {
        accountRepository.delete(accountId);
    }
}
