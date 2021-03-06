package ru.romanov.mta.service;

import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mta.servlet.model.TransferModel;

import java.util.concurrent.locks.ReentrantLock;

public class TransferService {

    private AccountRepository accountRepository = new AccountRepository();

    private AccountLockService accountLockService = AccountLockService.getInstance();

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
        long senderId = transferModel.getSenderId();
        long recipientId = transferModel.getRecipientId();

        ReentrantLock lock1;
        ReentrantLock lock2;

        if (senderId < recipientId) {
            lock1 = accountLockService.getLock(senderId);
            lock2 = accountLockService.getLock(recipientId);
        } else {
            lock1 = accountLockService.getLock(recipientId);
            lock2 = accountLockService.getLock(senderId);
        }

        lock1.lock();
        lock2.lock();
        try {
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
        } finally {
            lock2.unlock();
            lock1.unlock();
        }
    }
}
