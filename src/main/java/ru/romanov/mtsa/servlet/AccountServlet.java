package ru.romanov.mtsa.servlet;

import org.apache.tomcat.util.buf.StringUtils;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.service.impl.AccountServiceImpl;
import ru.romanov.mtsa.servlet.exception.AccountJsonValidationException;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/account")
public class AccountServlet {

    private AccountService accountService = AccountServiceImpl.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(){
        GenericEntity<List<AccountJsonModel>> responseBody;
        try {
            responseBody = new GenericEntity<List<AccountJsonModel>>(accountService.getAccounts()) {};
        } catch (ApplicationPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.OK).entity(responseBody).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        AccountJsonModel account;
        try {
            long accountId = Long.parseLong(id);
            account = accountService.getAccount(accountId);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchAccountException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ApplicationPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountJsonModel accountJsonModel) {
        AccountJsonModel responseBody;
        try {
            validateAccountJson(accountJsonModel);
            responseBody = accountService.createAccount(accountJsonModel);
        } catch (AccountJsonValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (ApplicationPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).entity(responseBody).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountJsonModel accountJsonModel) {
        try {
            validateAccountJson(accountJsonModel);
            accountService.updateAccount(accountJsonModel);
        } catch (AccountJsonValidationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchAccountException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ApplicationPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).entity(accountJsonModel).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        try {
            long accountId = Long.parseLong(id);
            accountService.deleteAccount(accountId);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (NoSuchAccountException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (ApplicationPersistenceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).build();
    }

    private void validateAccountJson(AccountJsonModel accountJsonModel) throws AccountJsonValidationException {
        if (null == accountJsonModel.getHolderName()) {
            throw new AccountJsonValidationException("Account holder name can not be null");
        } else if (accountJsonModel.getBalance() < 0.0) {
            throw new AccountJsonValidationException("Account can not have negative balance");
        }
    }
}
