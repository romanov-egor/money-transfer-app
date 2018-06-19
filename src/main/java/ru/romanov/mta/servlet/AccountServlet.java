package ru.romanov.mta.servlet;

import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.service.AccountService;
import ru.romanov.mta.servlet.exception.AccountModelValidationException;
import ru.romanov.mta.servlet.model.AccountModel;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides RESTful API to manage Accounts
 *
 * @author Egor Romanov
 */
@Path("/account")
public class AccountServlet extends AbstractServlet {

    public static final Logger log = Logger.getLogger(AccountServlet.class.getName());

    private final AccountService accountService = new AccountService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(){
        GenericEntity<List<AccountModel>> responseBody;
        try {
            responseBody = new GenericEntity<List<AccountModel>>(accountService.getAccounts()) {};
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return buildSuccessResponse(responseBody);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        AccountModel accountModel;
        try {
            long accountId = Long.parseLong(id);
            accountModel = accountService.getAccount(accountId);
        } catch (NumberFormatException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Wrong number format in request path");
        } catch (NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(accountModel);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountModel accountModel) {
        AccountModel responseBody;
        try {
            validateAccountModel(accountModel);
            responseBody = accountService.createAccount(accountModel);
        } catch (AccountModelValidationException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(responseBody);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountModel accountModel) {
        try {
            validateAccountModel(accountModel);
            accountService.updateAccount(accountModel);
        } catch (AccountModelValidationException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(accountModel);
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAccount(@PathParam("id") String id) {
        try {
            long accountId = Long.parseLong(id);
            accountService.deleteAccount(accountId);
        } catch (NumberFormatException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Wrong number format in request path");
        } catch (NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse();
    }

    private void validateAccountModel(AccountModel accountModel) throws AccountModelValidationException {
        if (null == accountModel.getHolderName()) {
            throw new AccountModelValidationException("Account holder name can not be null");
        } else if (accountModel.getBalance() < 0.0) {
            throw new AccountModelValidationException("Account can not have negative balance");
        }
    }
}
