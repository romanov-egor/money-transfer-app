package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mtsa.service.TransferService;
import ru.romanov.mtsa.service.impl.TransferServiceImpl;
import ru.romanov.mtsa.servlet.exception.TransferJsonValidationException;
import ru.romanov.mtsa.servlet.model.TransferJson;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Provides API to transfer money between accounts
 *
 * @author Egor Romanov
 */
@Path("/transfer")
public class TransferServlet extends AbstractServlet {

    private TransferService transferService = TransferServiceImpl.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferJson transferJson) {
        try {
            validateTransferJson(transferJson);
            transferService.transfer(transferJson);
        } catch (TransferJsonValidationException | NotEnoughMoneyForTransferException | NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return buildSuccessResponse();
    }

    private void validateTransferJson(TransferJson transferJson) throws TransferJsonValidationException {
        if (transferJson.getSenderId() == 0 || transferJson.getRecipientId() == 0) {
            throw new TransferJsonValidationException("Incorrect transfer participants data");
        } else if (transferJson.getSenderId() == transferJson.getRecipientId()) {
            throw new TransferJsonValidationException("Sender is the same as Recipient");
        } else if (transferJson.getTransferAmount() < 0.0) {
            throw new TransferJsonValidationException("Transfer amount can not be negative");
        }
    }
}
