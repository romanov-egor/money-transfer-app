package ru.romanov.mta.service;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.*;
import ru.romanov.mta.persistence.HibernateSessionFactory;
import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;
import ru.romanov.mta.persistence.repository.AccountRepository;
import ru.romanov.mta.service.converter.ModelConverter;
import ru.romanov.mta.service.exception.NotEnoughMoneyForTransferException;
import ru.romanov.mta.servlet.TransferServlet;
import ru.romanov.mta.servlet.model.AccountModel;
import ru.romanov.mta.servlet.model.TransferModel;

import javax.ws.rs.core.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TransferServiceTest extends JerseyTest {

    private static final int EXECUTOR_THREADS_COUNT = 4;
    private static final int EXECUTIONS_COUNT = 100;
    private static final int TEST_ACCOUNTS_COUNT = 10;
    private static final double ACCOUNT_MIN_BALANCE = 1000.0;

    private static TransferService transferService;
    private static AccountRepository accountRepository;

    private static List<AccountModel> storedAccounts;

    @BeforeClass
    public static void beforeClass() {
        transferService = new TransferService();
        accountRepository = new AccountRepository();
        storedAccounts = new ArrayList<>();

        populateDatabaseWithTestData();
    }

    @AfterClass
    public static void afterClass() {
        try {
            System.out.println(new AccountRepository().getAll());
        } catch (ApplicationPersistenceException e) {

        }
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
                account.setHolderName(Integer.toString(i));
                account.setBalance((i + 1) * ACCOUNT_MIN_BALANCE);
                accountRepository.create(account);
                storedAccounts.add(ModelConverter.toModel(account));
            }
        } catch (ApplicationPersistenceException e) {

        }
    }

    @Test
    public void transfer_whenParallelTransferAllFromOneSender_thenCorrectSenderBalance() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();
        Random random = new Random();
        double expectedResult = ACCOUNT_MIN_BALANCE;

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            TransferModel transferModel = new TransferModel();
            transferModel.setSenderId(1);
            //RecipientId must be from 2 to TEST_ACCOUNTS_COUNT - 1
            transferModel.setRecipientId(random.nextInt(TEST_ACCOUNTS_COUNT - 2) + 2);
            double transferAmount = random.nextDouble() * ACCOUNT_MIN_BALANCE / EXECUTIONS_COUNT;
            transferModel.setTransferAmount(transferAmount);
            transfers.add(transferModel);
            expectedResult -= transferAmount;
        }

        //When
        transfers.forEach(t -> {
            executorService.submit(() -> {
                try {
                    transferService.transfer(t);
                } catch (NotEnoughMoneyForTransferException | NoSuchAccountException e) {
                    System.out.println(e.getMessage() + " Wrong test data for " +
                            Thread.currentThread().getStackTrace()[1].getMethodName());
                } catch (ApplicationPersistenceException e) {
                    System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
                    e.printStackTrace();
                }
            });
        });
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Account actualResult = null;
        try {
            actualResult = accountRepository.get(1);
        } catch (NoSuchAccountException e) {
            System.out.println(e.getMessage() + " Wrong test data for " +
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
         if (actualResult != null) {
            assertEquals(expectedResult, actualResult.getBalance(), 0.01);
         } else {
            fail();
         }
    }

    /*@Test
    public void transfer_whenParallelTransferAllToOneRecipient_thenCorrectRecipientBalance() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();
        Random random = new Random();
        double expectedResult = ACCOUNT_MIN_BALANCE;

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            TransferModel transferModel = new TransferModel();
            //SenderId must be from 2 to TEST_ACCOUNTS_COUNT - 1
            transferModel.setSenderId(random.nextInt(TEST_ACCOUNTS_COUNT - 2) + 2);
            transferModel.setRecipientId(1);
            double transferAmount = random.nextDouble() * ACCOUNT_MIN_BALANCE / EXECUTIONS_COUNT;
            transferModel.setTransferAmount(transferAmount);
            transfers.add(transferModel);
            expectedResult -= transferAmount;
        }

        //When
        transfers.forEach(t -> {
            executorService.submit(() -> {
                try {
                    transferService.transfer(t);
                } catch (NotEnoughMoneyForTransferException | NoSuchAccountException e) {
                    System.out.println(e.getMessage() + " Wrong test data for " +
                            Thread.currentThread().getStackTrace()[1].getMethodName());
                } catch (ApplicationPersistenceException e) {
                    System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
                    e.printStackTrace();
                }
            });
        });
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Account actualResult = null;
        try {
            actualResult = accountRepository.get(1);
        } catch (NoSuchAccountException e) {
            System.out.println(e.getMessage() + " Wrong test data for " +
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
        if (actualResult != null) {
            assertEquals(expectedResult, actualResult.getBalance(), 0.01);
        } else {
            fail();
        }
    }*/
}
