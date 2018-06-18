package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.service.impl.AccountServiceImpl;
import ru.romanov.mtsa.servlet.exception.AccountJsonValidationException;
import ru.romanov.mtsa.servlet.model.AccountJson;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Provides RESTful API for Account entity
 *
 * @author Egor Romanov
 */
@Path("/account")
public class AccountServlet extends AbstractServlet {

    private AccountService accountService = AccountServiceImpl.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(){
        GenericEntity<List<AccountJson>> responseBody;
        try {
            responseBody = new GenericEntity<List<AccountJson>>(accountService.getAccounts()) {};
        } catch (ApplicationPersistenceException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return buildSuccessResponse(responseBody);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        AccountJson accountJson;
        try {
            long accountId = Long.parseLong(id);
            accountJson = accountService.getAccount(accountId);
        } catch (NumberFormatException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, "Wrong number format in request path");
        } catch (NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(accountJson);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountJson accountJson) {
        AccountJson responseBody;
        try {
            validateAccountJson(accountJson);
            responseBody = accountService.createAccount(accountJson);
        } catch (AccountJsonValidationException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(responseBody);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountJson accountJson) {
        try {
            validateAccountJson(accountJson);
            accountService.updateAccount(accountJson);
        } catch (AccountJsonValidationException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (NoSuchAccountException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } catch (ApplicationPersistenceException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse(accountJson);
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
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return buildSuccessResponse();
    }

    private void validateAccountJson(AccountJson accountJson) throws AccountJsonValidationException {
        if (null == accountJson.getHolderName()) {
            throw new AccountJsonValidationException("Account holder name can not be null");
        } else if (accountJson.getBalance() < 0.0) {
            throw new AccountJsonValidationException("Account can not have negative balance");
        }
    }
}
