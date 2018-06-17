package ru.romanov.mtsa.service;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

import java.util.List;

public interface AccountService {

    AccountJsonModel getAccount(long id) throws NoSuchAccountException, ApplicationPersistenceException;

    List<AccountJsonModel> getAccounts() throws ApplicationPersistenceException;

    AccountJsonModel createAccount(AccountJsonModel accountJsonModel) throws ApplicationPersistenceException;

    void updateAccount(AccountJsonModel accountJsonModel) throws NoSuchAccountException, ApplicationPersistenceException;

    void deleteAccount(long accountId) throws NoSuchAccountException, ApplicationPersistenceException;
}
