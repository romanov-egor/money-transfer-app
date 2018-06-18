package ru.romanov.mtsa.service;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.servlet.model.AccountJson;

import java.util.List;

/**
 * Service that provides business logic of Account CRUD operations. In current case it is only needed for scalability.
 *
 * @author Egor Romanov
 */
public interface AccountService {

    /**
     * Gets Account by identifier as presentation (json) model
     * @param id identifier of Account entity
     * @return Account presentation (json) model
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    AccountJson getAccount(long id) throws NoSuchAccountException, ApplicationPersistenceException;

    /**
     * Gets all Accounts as {@link List} of presentation (json) models
     * @return {@link List} of Account presentation (json) models
     * @throws ApplicationPersistenceException if problems with database connection
     */
    List<AccountJson> getAccounts() throws ApplicationPersistenceException;

    /**
     * Creates Account from presentation (json) model
     * @param accountJson json model with incoming parameters. {@link AccountJson#id} will be ignored
     * @return {@link AccountJson} that represents created {@link Account} entity (including it's identifier)
     * @throws ApplicationPersistenceException if problems with database connection
     */
    AccountJson createAccount(AccountJson accountJson) throws ApplicationPersistenceException;

    /**
     * Updates Account from presentation (json) model
     * @param accountJson json model with incoming parameters. {@link AccountJson#id} will be used to find entity to
     *                    update
     * @return {@link AccountJson} that represents updated {@link Account} entity
     * @throws NoSuchAccountException if no Account with {@link AccountJson#id} present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    void updateAccount(AccountJson accountJson) throws NoSuchAccountException, ApplicationPersistenceException;

    /**
     * Deletes Account by identifier
     * @param accountId identifier of Account entity
     * @throws NoSuchAccountException if no Account with such identifier present
     * @throws ApplicationPersistenceException if problems with database connection
     */
    void deleteAccount(long accountId) throws NoSuchAccountException, ApplicationPersistenceException;
}
