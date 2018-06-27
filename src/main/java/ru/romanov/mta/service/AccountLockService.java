package ru.romanov.mta.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class for account locks management. It's necessary to prevent simultaneous access to accounts
 * from different methods or threads
 *
 * @see AccountService
 * @see TransferService
 * @author Egor Romanov
 */
public class AccountLockService {

    private ConcurrentMap<Long, ReentrantLock> accountsLocks = new ConcurrentHashMap<>();

    private static AccountLockService instance;

    public static AccountLockService getInstance() {
        if (null == instance) {
            synchronized (AccountLockService.class) {
                if (null == instance) {
                    instance = new AccountLockService();
                }
            }
        }
        return instance;
    }

    private AccountLockService() {}

    /**
     * Creates or gets lock for account. Each account can have only one lock
     * @param accountId account identifier
     * @return lock for account
     */
    public ReentrantLock getLock(long accountId) {
        return accountsLocks.computeIfAbsent(accountId, (k) -> new ReentrantLock());
    }
}
