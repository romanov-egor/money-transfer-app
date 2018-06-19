package ru.romanov.mta.persistence.repository;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.romanov.mta.persistence.HibernateSessionFactory;
import ru.romanov.mta.persistence.entity.Account;
import ru.romanov.mta.persistence.exception.ApplicationPersistenceException;
import ru.romanov.mta.persistence.exception.NoSuchAccountException;

import java.util.List;

/**
 * Repository class to perform base persistence operation on {@link Account} entity
 *
 * @author Egor Romanov
 */
public class AccountRepository {

    public Account get(long id) throws NoSuchAccountException, ApplicationPersistenceException {
        Account account;
        try {
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            account = session.get(Account.class, id);
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Problems while executing #get() method. Check database connection.");
        }

        if (null == account) {
            throw new NoSuchAccountException("No account exists with id: " + id);
        }

        return account;
    }

    public List<Account> getAll() throws ApplicationPersistenceException {
        List<Account> accounts;
        try {
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            accounts = session.createQuery("from Account").list();
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Problems while executing #getAll() method. Check database connection.");
        }

        return accounts;
    }

    public Account create(Account account) throws ApplicationPersistenceException{
        Long id;
        try {
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            id = (Long) session.save(account);

            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Problems while executing #create() method. Check database connection.");
        }

        account.setId(id);
        return account;
    }

    public void update(Account account) throws NoSuchAccountException, ApplicationPersistenceException {
        //Existence check
        get(account.getId());

        try {
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            session.update(account);

            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Problems while executing #update() method. Check database connection.");
        }
    }

    public void delete(long id) throws NoSuchAccountException, ApplicationPersistenceException{
        //Existence check
        Account account = get(id);

        try {
            Session session = HibernateSessionFactory.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            session.delete(account);

            transaction.commit();
            session.close();
        } catch (HibernateException e) {
            throw new ApplicationPersistenceException("Problems while executing #delete() method. Check database connection.");
        }
    }
}
