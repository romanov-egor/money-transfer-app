package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mtsa.service.TransferService;
import ru.romanov.mtsa.service.impl.TransferServiceImpl;
import ru.romanov.mtsa.servlet.exception.TransferJsonValidationException;
import ru.romanov.mtsa.servlet.model.TransferJsonModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfer")
public class TransferServlet {

    private TransferService transferService = TransferServiceImpl.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferJsonModel transferJsonModel) {
        try {
            validateTransferJson(transferJsonModel);
            transferService.transfer(transferJsonModel);
        } catch (TransferJsonValidationException | NotEnoughMoneyForTransferException | NoSuchAccountException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (ApplicationPersistenceException e) {
            return Response.status((Response.Status.INTERNAL_SERVER_ERROR)).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    private void validateTransferJson(TransferJsonModel transferJsonModel) throws TransferJsonValidationException {
        if (transferJsonModel.getSenderId() == 0 || transferJsonModel.getRecipientId() == 0) {
            throw new TransferJsonValidationException("Incorrect transfer participants data");
        } else if (transferJsonModel.getSenderId() == transferJsonModel.getRecipientId()) {
            throw new TransferJsonValidationException("Sender is the same as Recipient");
        } else if (transferJsonModel.getTransferAmount() < 0.0) {
            throw new TransferJsonValidationException("Transfer amount can not be negative");
        }
    }
}
