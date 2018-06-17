package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.service.impl.AccountServiceImpl;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/accounts")
public class AccountServlet {

    private AccountService accountService = AccountServiceImpl.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(){
        GenericEntity<List<Account>> responseBody = new GenericEntity<List<Account>>(accountService.getAccounts()) {};
        return Response.status(Response.Status.OK).entity(responseBody).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        long accountId;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Account account = accountService.getAccount(accountId);

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountJsonModel accountJsonModel) {
        Account account = accountService.createAccount(accountJsonModel.getHolderName(), accountJsonModel.getBalance());

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(AccountJsonModel accountJsonModel) {
        Account account = accountService.updateAccount(accountJsonModel.getId(), accountJsonModel.getHolderName(), accountJsonModel.getBalance());

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        long accountId;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        accountService.deleteAccount(accountId);

        return Response.status(Response.Status.OK).build();
    }
}
