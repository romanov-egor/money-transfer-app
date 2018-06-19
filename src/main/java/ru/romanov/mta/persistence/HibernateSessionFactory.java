package ru.romanov.mta.persistence;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import ru.romanov.mta.persistence.entity.Account;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class wraps {@link SessionFactory}, performs build and close operations
 *
 * @author Egor Romanov
 */
public class HibernateSessionFactory {

    public static final Logger log = Logger.getLogger(HibernateSessionFactory.class.getName());

    private static volatile SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (HibernateSessionFactory.class) {
                if (sessionFactory == null) {
                    sessionFactory = initializeSessionFactory();
                }
            }
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (HibernateException ignore) {
                log.log(Level.SEVERE, "Unable to close SessionFactory", ignore);
            }
        }
    }

    private static SessionFactory initializeSessionFactory() {
        return new Configuration()
                .addProperties(getProperties())
                .addAnnotatedClass(Account.class)
                .buildSessionFactory();
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "org.hsqldb.jdbcDriver");
        properties.put(Environment.URL, "jdbc:hsqldb:mem:mta");
        properties.put(Environment.USER, "sa");
        properties.put(Environment.PASS, "");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");
        properties.put(Environment.HBM2DDL_AUTO, "create-drop");
        return properties;
    }
}
