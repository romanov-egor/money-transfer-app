package ru.romanov.mta.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;
import ru.romanov.mta.persistence.HibernateSessionFactory;
import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.service.converter.ModelConverter;
import ru.romanov.mta.servlet.model.AccountModel;
import ru.romanov.mta.servlet.model.TransferModel;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TransferServletTest extends JerseyTest {

    private static int TEST_ACCOUNTS_COUNT = 5;
    private static final double ACCOUNT_MIN_BALANCE = 1000.0;

    private static AccountRepository accountRepository;

    private static List<AccountModel> storedAccounts;

    @BeforeClass
    public static void beforeClass() {
        storedAccounts = new ArrayList<>();
        accountRepository = new AccountRepository();

        populateDatabaseWithTestData();
    }

    @AfterClass
    public static void afterClass() {
        HibernateSessionFactory.closeSessionFactory();
    }

    @Override
    protected Application configure() {
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig(TransferServlet.class, JacksonFeature.class);
    }

    private static void populateDatabaseWithTestData() {
        try {
            for (int i = 0; i < TEST_ACCOUNTS_COUNT; i++) {
                Account account = new Account();
                account.setHolderName(String.valueOf(i));
                account.setBalance((i + 1) * ACCOUNT_MIN_BALANCE);
                accountRepository.create(account);
                storedAccounts.add(ModelConverter.toModel(account));
            }
        } catch (ApplicationPersistenceException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------
    //--- Tests of negative scenarios ---
    //-----------------------------------

    @Test
    public void transfer_whenSenderIdEqualsRecipientId_thenBadRequest() {
        //Given
        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(randomSender.getId());
        transferModel.setRecipientId(randomSender.getId());
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
        Random random = new Random();
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(0);
        transferModel.setRecipientId(randomRecipient.getId());
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
        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(randomSender.getId());
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
        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        while (randomSender.getId() == randomRecipient.getId()) {
            randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        }

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(randomSender.getId());
        transferModel.setRecipientId(randomRecipient.getId());
        transferModel.setTransferAmount(randomSender.getBalance() + 1.0);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transfer_whenTransferAmountIsNegative_thenBadRequest() {
        //Given
        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        while (randomSender.getId() == randomRecipient.getId()) {
            randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        }

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(randomSender.getId());
        transferModel.setRecipientId(randomRecipient.getId());
        transferModel.setTransferAmount(-1.0);
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
        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        while (randomSender.getId() == randomRecipient.getId()) {
            randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        }

        TransferModel transferModel = new TransferModel();
        transferModel.setSenderId(randomSender.getId());
        transferModel.setRecipientId(randomRecipient.getId());
        double transferAmount = random.nextDouble() * randomSender.getBalance();
        transferModel.setTransferAmount(transferAmount);
        Entity<TransferModel> transferEntity = Entity.entity(transferModel, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/transfer").request().post(transferEntity);

        //Then
        randomSender.setBalance(randomSender.getBalance() - transferAmount);
        randomRecipient.setBalance(randomRecipient.getBalance() + transferAmount);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
