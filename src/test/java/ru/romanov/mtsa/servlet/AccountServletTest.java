package ru.romanov.mtsa.servlet;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.repository.AccountRepository;
import ru.romanov.mtsa.servlet.model.AccountJsonModel;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

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
    public void createAccount_whenHolderNameIsNull_thenBadRequest() {
        //Given
        Account account = new Account();
        account.setBalance(0.0);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().post(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        Account account = new Account();
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

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
        Account account = new Account();
        account.setId(1);
        account.setBalance(0.0);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenBalanceIsNegative_thenBadRequest() {
        //Given
        Account account = new Account();
        account.setId(1);
        account.setHolderName("");
        account.setBalance(-1.0);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

        //When
        Response response = target("/account").request().put(accountEntity);

        //Then
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void updateAccount_whenAccountDoesNotExists_thenNotFound() {
        //Given
        Account account = new Account();
        account.setId(0);
        account.setHolderName("");
        account.setBalance(0.0);
        Entity<Account> accountEntity = Entity.entity(account, MediaType.APPLICATION_JSON);

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
        response.close();
    }

    @Test
    public void deleteAccount_whenAccountDoesNotExists_thenNotFound() {
        //When
        Response response = target("/account/0").request().delete();

        //Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAccount_whenAccountDoesNotExists_thenNotFound() {
        //When
        Response response = target("/account/0").request().get();

        //Then
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
}
