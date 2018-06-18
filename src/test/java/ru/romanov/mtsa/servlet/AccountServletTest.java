package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.AccountModel;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccountServletTest extends JerseyTest {

    @Override
    protected Application configure() {
        populateDatabaseWithTestData();
        forceSet(TestProperties.CONTAINER_PORT, "0");
        return new ResourceConfig(AccountServlet.class, JacksonFeature.class);
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
        AccountModel account = new AccountModel();
        account.setId(1);
        account.setBalance(0.0);
        Entity<AccountModel> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        AccountModel account = new AccountModel();
        account.setId(1);
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<AccountModel> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

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
        AccountModel expectedAccountModel = new AccountModel();
        expectedAccountModel.setId(1);
        expectedAccountModel.setHolderName("a");
        expectedAccountModel.setBalance(11.0);

        //When
        Response response = target("/account/1").request().get();

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
        AccountModel accountToCreate = new AccountModel();
        accountToCreate.setHolderName("d");
        accountToCreate.setBalance(44.0);
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
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void updateAccount_whenUpdateAccountSucceed_thenOkAndAccountJson() {
        //Given
        AccountModel accountToUpdate = new AccountModel();
        accountToUpdate.setId(2);
        accountToUpdate.setHolderName("b_u");
        accountToUpdate.setBalance(23.0);
        Entity<AccountModel> accountEntity = Entity.entity(accountToUpdate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        AccountModel receivedAccountModel = response.readEntity(AccountModel.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(accountToUpdate.equals(receivedAccountModel));
    }

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void deleteAccount_whenDeleteAccountSucceed_thenOk() {
        //When
        Response response = target("/account/3").request().delete();

        //Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
