package ru.romanov.mtsa.converter;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

public class ModelConverter {

    public static AccountJsonModel toJsonModel(Account account) {
        AccountJsonModel accountJsonModel = new AccountJsonModel();
        accountJsonModel.setId(account.getId());
        accountJsonModel.setHolderName(account.getHolderName());
        accountJsonModel.setBalance(account.getBalance());
        return accountJsonModel;
    }

    public static Account toPersistenceModel(AccountJsonModel accountJsonModel) {
        Account account = new Account();
        account.setId(accountJsonModel.getId());
        account.setHolderName(accountJsonModel.getHolderName());
        account.setBalance(accountJsonModel.getBalance());
        return account;
    }
}
