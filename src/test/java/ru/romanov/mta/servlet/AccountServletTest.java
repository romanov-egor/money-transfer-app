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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServletTest extends JerseyTest {

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
        return new ResourceConfig(AccountServlet.class, JacksonFeature.class);
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
    public void createAccount_whenHolderNameIsNull_thenBadRequest() {
        //Given
        AccountModel account = new AccountModel();
        account.setBalance(0.0);
        Entity<AccountModel> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        AccountModel account = new AccountModel();
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<AccountModel> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createAccount_whenMalformedJson_thenBadRequest() {
        //Given
        Entity<String> accountEntity = Entity.entity("Malformed json.", MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenHolderNameIsNull_thenBadRequest() {
        //Given
        Random random = new Random();
        AccountModel randomAccount = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        AccountModel accountToUpdate = new AccountModel();
        accountToUpdate.setId(randomAccount.getId());
        accountToUpdate.setHolderName(null);
        accountToUpdate.setBalance(randomAccount.getBalance());
        Entity<AccountModel> accountEntity = Entity.entity(accountToUpdate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        Random random = new Random();
        AccountModel randomAccount = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        AccountModel accountToUpdate = new AccountModel();
        accountToUpdate.setId(randomAccount.getId());
        accountToUpdate.setHolderName(randomAccount.getHolderName());
        accountToUpdate.setBalance(-1.0);
        Entity<AccountModel> accountEntity = Entity.entity(accountToUpdate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenAccountDoesNotExist_thenNotFound() {
        //Given
        AccountModel account = new AccountModel();
        account.setId(0);
        account.setHolderName("");
        account.setBalance(0.0);
        Entity<AccountModel> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenMalformedJson_thenBadRequest() {
        //Given
        Entity<String> accountEntity = Entity.entity("Malformed json.", MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void deleteAccount_whenAccountDoesNotExist_thenNotFound() {
        //When
        Response response = target("/account/0").request().delete();

        //Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAccount_whenAccountDoesNotExist_thenNotFound() {
        //When
        Response response = target("/account/0").request().get();

        //Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    //-----------------------------------
    //--- Tests of positive scenarios ---
    //-----------------------------------

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void getAccount_whenAccountExists_thenOkAndAccountJson() {
        //Given
        Random random = new Random();
        AccountModel expectedAccountModel = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        //When
        Response response = target("/account/" + expectedAccountModel.getId()).request().get();

        //Then
        AccountModel receivedAccountModel = response.readEntity(AccountModel.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(expectedAccountModel.equals(receivedAccountModel));
    }

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void createAccount_whenCreateAccountSucceed_thenOkAndAccountJson() {
        //Given
        Random random = new Random();
        AccountModel accountToCreate = new AccountModel();
        accountToCreate.setHolderName(String.valueOf(random.nextInt()));
        accountToCreate.setBalance((random.nextDouble() + 1) * ACCOUNT_MIN_BALANCE);
        Entity<AccountModel> accountEntity = Entity.entity(accountToCreate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        AccountModel receivedAccountModel = response.readEntity(AccountModel.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(receivedAccountModel.getId() > 0);
        assertEquals(accountToCreate.getHolderName(), receivedAccountModel.getHolderName());
        assertEquals(accountToCreate.getBalance(), receivedAccountModel.getBalance(), 0);
    }

    /**
     * Not thread safe test
     */
    @Test
    public void updateAccount_whenUpdateAccountSucceed_thenOkAndAccountJson() {
        //Given
        Random random = new Random();
        AccountModel accountToUpdate = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        accountToUpdate.setHolderName(String.valueOf(random.nextInt()));
        accountToUpdate.setBalance((random.nextDouble() + 1) * ACCOUNT_MIN_BALANCE);
        Entity<AccountModel> accountEntity = Entity.entity(accountToUpdate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        AccountModel receivedAccountModel = response.readEntity(AccountModel.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(accountToUpdate.equals(receivedAccountModel));
    }

    /**
     * Not thread safe test
     */
    @Test
    public void deleteAccount_whenDeleteAccountSucceed_thenOk() {
        //Given
        Random random = new Random();
        AccountModel accountToDelete = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        long accountToDeleteId = accountToDelete.getId();

        //When
        Response response = target("/account/" + accountToDeleteId).request().delete();

        //Then
        TEST_ACCOUNTS_COUNT--;
        storedAccounts.remove(accountToDeleteId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
