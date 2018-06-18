package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.AccountJson;

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
    public void createAccount_whenHolderNameIsNull_thenBadRequest() {
        //Given
        AccountJson account = new AccountJson();
        account.setBalance(0.0);
        Entity<AccountJson> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        AccountJson account = new AccountJson();
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<AccountJson> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

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
        AccountJson account = new AccountJson();
        account.setId(1);
        account.setBalance(0.0);
        Entity<AccountJson> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        AccountJson account = new AccountJson();
        account.setId(1);
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<AccountJson> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenAccountDoesNotExist_thenNotFound() {
        //Given
        AccountJson account = new AccountJson();
        account.setId(0);
        account.setHolderName("");
        account.setBalance(0.0);
        Entity<AccountJson> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

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
        AccountJson expectedAccountJson = new AccountJson();
        expectedAccountJson.setId(1);
        expectedAccountJson.setHolderName("a");
        expectedAccountJson.setBalance(11.0);

        //When
        Response response = target("/account/1").request().get();

        //Then
        AccountJson receivedAccountJson = response.readEntity(AccountJson.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(expectedAccountJson.equals(receivedAccountJson));
    }

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void createAccount_whenCreateAccountSucceed_thenOkAndAccountJson() {
        //Given
        AccountJson accountToCreate = new AccountJson();
        accountToCreate.setHolderName("d");
        accountToCreate.setBalance(44.0);
        Entity<AccountJson> accountEntity = Entity.entity(accountToCreate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        AccountJson receivedAccountJson = response.readEntity(AccountJson.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(receivedAccountJson.getId() > 0);
        assertEquals(accountToCreate.getHolderName(), receivedAccountJson.getHolderName());
        assertEquals(accountToCreate.getBalance(), receivedAccountJson.getBalance(), 0);
    }

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void updateAccount_whenUpdateAccountSucceed_thenOkAndAccountJson() {
        //Given
        AccountJson accountToUpdate = new AccountJson();
        accountToUpdate.setId(3);
        accountToUpdate.setHolderName("c_u");
        accountToUpdate.setBalance(34.0);
        Entity<AccountJson> accountEntity = Entity.entity(accountToUpdate, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        AccountJson receivedAccountJson = response.readEntity(AccountJson.class);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(accountToUpdate.equals(receivedAccountJson));
    }

    /**
     * This test based on database initialization data.
     * @see #populateDatabaseWithTestData()
     */
    @Test
    public void deleteAccount_whenDeleteAccountSucceed_thenOk() {
        //When
        Response response = target("/account/2").request().delete();

        //Then
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
