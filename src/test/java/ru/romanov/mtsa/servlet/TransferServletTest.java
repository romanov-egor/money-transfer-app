package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.TransferJsonModel;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class TransferServletTest extends JerseyTest {

    @Override
    protected Application configure() {
        populateDatabaseWithTestData();
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig(TransferServlet.class, JacksonFeature.class);
    }

    private void populateDatabaseWithTestData() {
        AccountRepository accountRepository = new AccountRepository();

        Account account = new Account();
        account.setHolderName("1");
        account.setBalance(1.0);
        accountRepository.create(account);

        account.setHolderName("2");
        account.setBalance(2.0);
        accountRepository.create(account);

        account.setHolderName("3");
        account.setBalance(3.0);
        accountRepository.create(account);
    }

    @Test
    public void transfer_whenSenderIdEqualsRecipientId_thenBadRequest() {
        //Given
        TransferJsonModel transferJsonModel = new TransferJsonModel();
        transferJsonModel.setSenderId(1);
        transferJsonModel.setRecipientId(1);
        transferJsonModel.setTransferAmount(1.0);
        Entity<TransferJsonModel> transferEntity = Entity.entity(transferJsonModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderDoesNotExist_thenBadRequest() {
        //Given
        TransferJsonModel transferJsonModel = new TransferJsonModel();
        transferJsonModel.setSenderId(0);
        transferJsonModel.setRecipientId(1);
        transferJsonModel.setTransferAmount(1.0);
        Entity<TransferJsonModel> transferEntity = Entity.entity(transferJsonModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenRecipientDoesNotExist_thenBadRequest() {
        //Given
        TransferJsonModel transferJsonModel = new TransferJsonModel();
        transferJsonModel.setSenderId(1);
        transferJsonModel.setRecipientId(0);
        transferJsonModel.setTransferAmount(1.0);
        Entity<TransferJsonModel> transferEntity = Entity.entity(transferJsonModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderBalanceIsLessThanTransferAmount_thenBadRequest() {
        //Given
        TransferJsonModel transferJsonModel = new TransferJsonModel();
        transferJsonModel.setSenderId(1);
        transferJsonModel.setRecipientId(2);
        transferJsonModel.setTransferAmount(10.0);
        Entity<TransferJsonModel> transferEntity = Entity.entity(transferJsonModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenMalformedJson_thenBadRequest() {
        //Given
        Entity<String> transferEntity = Entity.entity("Malformed json.", MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
}
