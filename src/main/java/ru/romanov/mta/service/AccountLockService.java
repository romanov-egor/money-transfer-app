package ru.romanov.mta.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

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

    public ReentrantLock getLock(long accountId) {
        return accountsLocks.computeIfAbsent(accountId, (k) -> new ReentrantLock());
    }
}
