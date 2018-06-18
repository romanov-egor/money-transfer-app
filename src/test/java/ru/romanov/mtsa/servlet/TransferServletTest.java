package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.TransferModel;

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

        try {
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
        } catch (ApplicationPersistenceException ignore) {}
    }

    //-----------------------------------
    //--- Tests of negative scenarios ---
    //-----------------------------------

    @Test
    public void transfer_whenSenderIdEqualsRecipientId_thenBadRequest() {
        //Given
        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(1);
        transferModel.setRecipientId(1);
        transferModel.setTransferAmount(1.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderDoesNotExist_thenBadRequest() {
        //Given
        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(0);
        transferModel.setRecipientId(1);
        transferModel.setTransferAmount(1.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenRecipientDoesNotExist_thenBadRequest() {
        //Given
        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(1);
        transferModel.setRecipientId(0);
        transferModel.setTransferAmount(1.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenSenderBalanceIsLessThanTransferAmount_thenBadRequest() {
        //Given
        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(1);
        transferModel.setRecipientId(2);
        transferModel.setTransferAmount(50.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

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

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void transfer_whenTransferSucceed_thenOk() {
        //Given
        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(1);
        transferModel.setRecipientId(2);
        transferModel.setTransferAmount(5.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
