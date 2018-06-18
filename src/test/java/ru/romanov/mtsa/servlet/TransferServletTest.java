package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.TransferJson;

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
        account.setHolderName("a");
        account.setBalance(11.0);
        accountRepository.create(account);

        account.setHolderName("b");
        account.setBalance(22.0);
        accountRepository.create(account);

        account.setHolderName("c");
        account.setBalance(33.0);
        accountRepository.create(account);
    }

    //-----------------------------------
    //--- Tests of negative scenarios ---
    //-----------------------------------

    @Test
    public void transfer_whenSenderIdEqualsRecipientId_thenBadRequest() {
        //Given
        TransferJson transferJson = new TransferJson();
        transferJson.setSenderId(1);
        transferJson.setRecipientId(1);
        transferJson.setTransferAmount(1.0);
        Entity<TransferJson> transferEntity = Entity.entity(transferJson, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderDoesNotExist_thenBadRequest() {
        //Given
        TransferJson transferJson = new TransferJson();
        transferJson.setSenderId(0);
        transferJson.setRecipientId(1);
        transferJson.setTransferAmount(1.0);
        Entity<TransferJson> transferEntity = Entity.entity(transferJson, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenRecipientDoesNotExist_thenBadRequest() {
        //Given
        TransferJson transferJson = new TransferJson();
        transferJson.setSenderId(1);
        transferJson.setRecipientId(0);
        transferJson.setTransferAmount(1.0);
        Entity<TransferJson> transferEntity = Entity.entity(transferJson, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderBalanceIsLessThanTransferAmount_thenBadRequest() {
        //Given
        TransferJson transferJson = new TransferJson();
        transferJson.setSenderId(1);
        transferJson.setRecipientId(2);
        transferJson.setTransferAmount(10.0);
        Entity<TransferJson> transferEntity = Entity.entity(transferJson, MediaType.APPLICATION_JSON);

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

    //-----------------------------------
    //--- Tests of positive scenarios ---
    //-----------------------------------


}
