package ru.romanov.mtsa.service.impl;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.service.TransferService;
import ru.romanov.mtsa.servlet.model.TransferJsonModel;

public class TransferServiceImpl implements TransferService {

    private static volatile TransferService instance;

    private AccountRepository accountRepository = new AccountRepository();

    public static TransferService getInstance() {
        if (instance == null) {
            synchronized (TransferServiceImpl.class) {
                if (instance == null) {
                    instance = new TransferServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void transfer(TransferJsonModel transferJsonModel) throws NoSuchAccountException,
            NotEnoughMoneyForTransferException, ApplicationPersistenceException {
        Account sender;
        Account recipient;
        try {
            sender = accountRepository.get(transferJsonModel.getSenderId());
        } catch (NoSuchAccountException e) {
            throw new NoSuchAccountException("Unable to get sender account with id: " + transferJsonModel.getSenderId());
        }

        try {
            recipient = accountRepository.get(transferJsonModel.getRecipientId());
        } catch (NoSuchAccountException e) {
            throw new NoSuchAccountException("Unable to get recipient account with id: " + transferJsonModel.getRecipientId());
        }

        double amount = transferJsonModel.getTransferAmount();
        if (sender.getBalance() - amount < 0.0) {
            throw new NotEnoughMoneyForTransferException("Sender does not have enough money to perform transfer operation");
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        accountRepository.update(sender);
        accountRepository.update(recipient);
    }
}