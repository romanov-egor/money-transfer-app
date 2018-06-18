package ru.romanov.mtsa.converter;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.servlet.model.AccountJson;

/**
 * Converts entities from presentation to persistent models and vice versa
 *
 * @author Egor Romanov
 */
public class ModelConverter {

    public static AccountJson toJsonModel(Account account) {
        AccountJson accountJson = new AccountJson();
        accountJson.setId(account.getId());
        accountJson.setHolderName(account.getHolderName());
        accountJson.setBalance(account.getBalance());
        return accountJson;
    }

    public static Account toPersistenceModel(AccountJson accountJson) {
        Account account = new Account();
        account.setId(accountJson.getId());
        account.setHolderName(accountJson.getHolderName());
        account.setBalance(accountJson.getBalance());
        return account;
    }
}
