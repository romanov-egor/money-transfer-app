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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TransferServiceTest extends JerseyTest {

    private static final int EXECUTOR_THREADS_COUNT = 4;
    private static final int EXECUTIONS_COUNT = 40;
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
        HibernateSessionFactory.closeSessionFactory();
    }

    @Before
    public void setUpAccounts() {
        try {
            List<Account> accounts = accountRepository.getAll();
            storedAccounts = accounts.stream().map(ModelConverter::toModel).collect(Collectors.toList());
        } catch (ApplicationPersistenceException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

    /**
     * Check sender balance update in concurrent environment
     */
    @Test
    public void transfer_whenConcurrentTransferAllFromOneSender_thenCorrectSenderBalance() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();

        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        double expectedResult = randomSender.getBalance();
        double minBalance = storedAccounts.stream().min((a1, a2) -> a1.getBalance() < a2.getBalance() ? -1 : 1)
                .get().getBalance();

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            TransferModel transferModel = new TransferModel();
            transferModel.setSenderId(randomSender.getId());

            AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            while (randomSender.getId() == randomRecipient.getId()) {
                randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            }

            transferModel.setRecipientId(randomRecipient.getId());
            double transferAmount = random.nextDouble() * minBalance / EXECUTIONS_COUNT;
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
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Double actualResult = null;
        try {
            actualResult = accountRepository.get(randomSender.getId()).getBalance();
        } catch (NoSuchAccountException e) {
            System.out.println(e.getMessage() + " Wrong test data for " +
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
         if (actualResult != null) {
            assertEquals(expectedResult, actualResult, 0.01);
         } else {
            fail();
         }
    }

    /**
     * Check recipient balance update in concurrent environment
     */
    @Test
    public void transfer_whenConcurrentTransferAllToOneRecipient_thenCorrectRecipientBalance() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();

        Random random = new Random();
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));

        double expectedResult = randomRecipient.getBalance();
        double minBalance = storedAccounts.stream().min((a1, a2) -> a1.getBalance() < a2.getBalance() ? -1 : 1)
                .get().getBalance();

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            TransferModel transferModel = new TransferModel();
            transferModel.setRecipientId(randomRecipient.getId());

            AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            while (randomRecipient.getId() == randomSender.getId()) {
                randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            }

            transferModel.setSenderId(randomSender.getId());
            double transferAmount = random.nextDouble() * minBalance / EXECUTIONS_COUNT;
            transferModel.setTransferAmount(transferAmount);
            transfers.add(transferModel);
            expectedResult += transferAmount;
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
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Double actualResult = null;
        try {
            actualResult = accountRepository.get(randomRecipient.getId()).getBalance();
        } catch (NoSuchAccountException e) {
            System.out.println(e.getMessage() + " Wrong test data for " +
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
        if (actualResult != null) {
            assertEquals(expectedResult, actualResult, 0.01);
        } else {
            fail();
        }
    }

    /**
     * Check that sender-recipient transfers never reach race condition
     */
    @Test
    public void transfer_whenConcurrentTransferBetweenTwoAccounts_thenCorrectBalancesSum() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();

        Random random = new Random();
        AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        while (randomRecipient.getId() == randomSender.getId()) {
            randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
        }

        double expectedResult = randomRecipient.getBalance() + randomSender.getBalance();
        double minBalance = randomSender.getBalance() < randomRecipient.getBalance() ? randomSender.getBalance()
                : randomRecipient.getBalance();

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            TransferModel transferModel = new TransferModel();
            if (random.nextBoolean()) {
                transferModel.setSenderId(randomSender.getId());
                transferModel.setRecipientId(randomRecipient.getId());
            } else {
                transferModel.setSenderId(randomRecipient.getId());
                transferModel.setRecipientId(randomSender.getId());
            }
            double transferAmount = random.nextDouble() * minBalance / EXECUTIONS_COUNT;
            transferModel.setTransferAmount(transferAmount);
            transfers.add(transferModel);
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
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Double actualResult = null;
        try {
            Account actualSender = accountRepository.get(randomSender.getId());
            Account actualRecipient = accountRepository.get(randomRecipient.getId());
            actualResult = actualSender.getBalance() + actualRecipient.getBalance();
        } catch (NoSuchAccountException e) {
            System.out.println(e.getMessage() + " Wrong test data for " +
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
        if (actualResult != null) {
            assertEquals(expectedResult, actualResult, 0.01);
        } else {
            fail();
        }
    }

    /**
     * Check common money transfer use case in concurrent environment
     */
    @Test
    public void transfer_whenConcurrentTransferBetweenManyAccounts_thenCorrectBalancesSum() {
        //Given
        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREADS_COUNT);
        List<TransferModel> transfers = new ArrayList<>();

        double expectedResult = storedAccounts.stream().mapToDouble(a -> a.getBalance()).sum();

        for (int i = 0; i < EXECUTIONS_COUNT; i++) {
            Random random = new Random();
            AccountModel randomSender = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            AccountModel randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            while (randomRecipient.getId() == randomSender.getId()) {
                randomRecipient = storedAccounts.get(random.nextInt(TEST_ACCOUNTS_COUNT));
            }

            double minBalance = randomSender.getBalance() < randomRecipient.getBalance() ? randomSender.getBalance()
                    : randomRecipient.getBalance();

            TransferModel transferModel = new TransferModel();
            transferModel.setSenderId(randomSender.getId());
            transferModel.setRecipientId(randomRecipient.getId());
            double transferAmount = random.nextDouble() * minBalance / EXECUTIONS_COUNT;
            transferModel.setTransferAmount(transferAmount);
            transfers.add(transferModel);
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
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Then
        Double actualResult = null;
        try {
            List<Account> accounts = accountRepository.getAll();
            storedAccounts = accounts.stream().map(ModelConverter::toModel).collect(Collectors.toList());
            actualResult = storedAccounts.stream().mapToDouble(a -> a.getBalance()).sum();
        } catch (ApplicationPersistenceException e) {
            System.out.println("Wrong test config for class " + TransferServiceTest.class.getName());
            e.printStackTrace();
        }
        if (actualResult != null) {
            assertEquals(expectedResult, actualResult, 0.01);
        } else {
            fail();
        }
    }
}
