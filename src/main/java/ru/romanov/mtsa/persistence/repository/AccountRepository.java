package ru.romanov.mtsa.persistence.repository;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.romanov.mtsa.persistence.HibernateSessionFactory;
import ru.romanov.mtsa.persistence.entity.Account;

import java.util.List;

public class AccountRepository {

    public Account get(long id) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        Account account = session.get(Account.class, id);

        session.close();

        return account;
    }

    public List<Account> getAll() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();

        List<Account> accounts = session.createQuery("from Account").list();

        session.close();

        return accounts;
    }

    public long create(String holderName, double balance) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Account account = new Account();
        account.setHolderName(holderName);
        account.setBalance(balance);

        Long id = (Long) session.save(account);

        transaction.commit();
        session.close();

        return id;
    }

    public void update(Account account) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.update(account);

        transaction.commit();
        session.close();
    }

    public void delete(long id) {
        Account account = get(id);

        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.delete(account);

        transaction.commit();
        session.close();
    }
}
