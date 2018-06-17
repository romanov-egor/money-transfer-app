package ru.romanov.mtsa.servlet;

import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.service.AccountService;
import ru.romanov.mtsa.service.impl.AccountServiceImpl;
import ru.romanov.mtsa.servlet.model.TransferJsonModel;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfer")
public class TransferServlet {

    private AccountService accountService = AccountServiceImpl.getInstance();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response transferMoney(TransferJsonModel transferData) {

        return Response.status(200).build();
    }
}
