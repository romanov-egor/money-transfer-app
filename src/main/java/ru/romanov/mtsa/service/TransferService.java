package ru.romanov.mtsa.service;

import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mtsa.servlet.model.TransferJsonModel;

public interface TransferService {

    void transfer(TransferJsonModel transferJsonModel) throws NoSuchAccountException,
            NotEnoughMoneyForTransferException, ApplicationPersistenceException;
}
