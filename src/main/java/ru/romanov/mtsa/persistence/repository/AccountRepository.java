package ru.romanov.mtsa.persistence.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.romanov.mtsa.persistence.HibernateSessionFactory;
import ru.romanov.mtsa.persistence.entity.Account;
import ru.romanov.mtsa.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mtsa.persistence.exception.NoSuchAccountException;

import java.util.List;

public class AccountRepository {

    public Account get(long id) throws NoSuchAccountException, ApplicationPersistenceException {
        Session session = openSession();
        Account account = session.get(Account.class, id);

        if (null == account) {
            throw new NoSuchAccountException("Unable to get account with id: " + id);
        }

        closeSession(session);
        return account;
    }

    public List<Account> getAll() throws ApplicationPersistenceException {
        Session session = openSession();

        List<Account> accounts = session.createQuery("from Account").list();

        closeSession(session);

        return accounts;
    }

    public Account create(Account account) throws ApplicationPersistenceException{
        Session session = openSession();
        Transaction transaction = session.beginTransaction();

        Long id = (Long) session.save(account);

        if (null == id) {
            throw new ApplicationPersistenceException("Unable to create new account");
        }

        transaction.commit();
        closeSession(session);

        account.setId(id);
        return account;
    }

    public void update(Account account) throws NoSuchAccountException, ApplicationPersistenceException {
        //Existence check
        get(account.getId());

        Session session = openSession();
        Transaction transaction = session.beginTransaction();

        session.update(account);

        transaction.commit();
        closeSession(session);
    }

    public void delete(long id) throws NoSuchAccountException, ApplicationPersistenceException{
        Account account = get(id);

        Session session = openSession();
        Transaction transaction = session.beginTransaction();

        session.delete(account);

        transaction.commit();
        closeSession(session);
    }

    private Session openSession() throws ApplicationPersistenceException {
        try {
            return HibernateSessionFactory.getSessionFactory().openSession();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Unable to open hibernate session", e);
        }
    }

    private void closeSession(Session session) {
        try {
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Unable to close hibernate session", e);
        }
    }
}
