package ru.romanov.mta.servlet;

import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.service.TransferService;
import ru.romanov.mta.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mta.servlet.exception.TransferModelValidationException;
import ru.romanov.mta.servlet.model.TransferModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides endpoint to transfer money between accounts
 *
 * @author Egor Romanov
 */
@Path("/transfer")
public class TransferServlet extends AbstractServlet {

    public static final Logger log = Logger.getLogger(TransferServlet.class.getName());

    private final TransferService transferService = new TransferService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferModel transferModel) {
        try {
            validateTransferModel(transferModel);
            transferService.transfer(transferModel);
        } catch (TransferModelValidationException | NotEnoughMoneyForTransferException | NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return buildSuccessResponse();
    }

    private void validateTransferModel(TransferModel transferModel) throws TransferModelValidationException {
        if (transferModel.getSenderId() == 0 || transferModel.getRecipientId() == 0) {
            throw new TransferModelValidationException("Incorrect transfer participants data");
        } else if (transferModel.getSenderId() == transferModel.getRecipientId()) {
            throw new TransferModelValidationException("Sender is the same as Recipient");
        } else if (transferModel.getTransferAmount() < 0.0) {
            throw new TransferModelValidationException("Transfer amount can not be negative");
        }
    }
}
