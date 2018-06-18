package ru.romanov.mtsa.service;

import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mtsa.servlet.model.TransferJson;

/**
 * Service to represent business logic of money transfer operations
 *
 * @author Egor Romanov
 */
public interface TransferService {

    /**
     * Transfer money between accounts
     * @param transferJson json model with incoming parameters
     * @throws NoSuchAccountException if no Account with {@link TransferJson#senderId} or
     * {@link TransferJson#recipientId} present
     * @throws NotEnoughMoneyForTransferException if Account with {@link TransferJson#senderId} has less money on
     * balance than {@link TransferJson#transferAmount}
     * @throws ApplicationPersistenceException if problems with database connection
     */
    void transfer(TransferJson transferJson) throws NoSuchAccountException,
            NotEnoughMoneyForTransferException, ApplicationPersistenceException;
}
