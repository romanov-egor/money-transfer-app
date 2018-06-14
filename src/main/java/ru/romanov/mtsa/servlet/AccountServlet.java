package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.service.impl.AccountServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounts")
public class AccountServlet {

    private AccountService accountService = AccountServiceImpl.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(){
        return Response.status(Response.Status.OK).entity(accountService.getAccounts()).build();
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
}
