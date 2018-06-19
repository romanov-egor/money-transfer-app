package ru.romanov.mta.service;

import ru.romanov.mta.service.converter.ModelConverter;
import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.servlet.model.AccountModel;

import java.util.List;
import java.util.stream.Collectors;

public class AccountService {

    private AccountRepository accountRepository = new AccountRepository();

    /**
     * Gets Account by identifier as {@link AccountModel}
     * @param id Account identifier
     * @return
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public AccountModel getAccount(long id) throws NoSuchAccountException, ApplicationPersistenceException {
        return ModelConverter.toModel(accountRepository.get(id));
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
        Account createdAccount = accountRepository.create(ModelConverter.toEntity(accountModel));
        return ModelConverter.toModel(createdAccount);
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
        accountRepository.update(ModelConverter.toEntity(accountModel));
    }

    /**
     * Deletes Account by identifier
     * @param accountId Account identifier
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public void deleteAccount(long accountId) throws NoSuchAccountException, ApplicationPersistenceException {
        accountRepository.delete(accountId);
    }
}
