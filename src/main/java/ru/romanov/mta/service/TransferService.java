package ru.romanov.mta.service;

import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.servlet.model.TransferModel;

public class TransferService {

    private AccountRepository accountRepository = new AccountRepository();

    /**
     * Transfer money between accounts
     * @param transferModel Model with incoming parameters
     * @throws NoSuchAccountException if no Account with {@link TransferModel#senderId} or
     * {@link TransferModel#recipientId} present
     * @throws NotEnoughMoneyForTransferException if Account with {@link TransferModel#senderId} has less money on
     * balance than {@link TransferModel#transferAmount}
     * @throws ApplicationPersistenceException if problems with database connection
     */
    public void transfer(TransferModel transferModel) throws NoSuchAccountException,
            NotEnoughMoneyForTransferException, ApplicationPersistenceException {
        Account sender;
        Account recipient;
        try {
            sender = accountRepository.get(transferModel.getSenderId());
        } catch (NoSuchAccountException e) {
            throw new NoSuchAccountException("Unable to get sender account with id: " + transferModel.getSenderId());
        }

        try {
            recipient = accountRepository.get(transferModel.getRecipientId());
        } catch (NoSuchAccountException e) {
            throw new NoSuchAccountException("Unable to get recipient account with id: " + transferModel.getRecipientId());
        }

        double amount = transferModel.getTransferAmount();
        if (sender.getBalance() - amount < 0.0) {
            throw new NotEnoughMoneyForTransferException("Sender does not have enough money to perform transfer operation");
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        accountRepository.update(sender);
        accountRepository.update(recipient);
    }
}
