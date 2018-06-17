package ru.romanov.mtsa.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import ru.romanov.mtsa.persistence.entity.Account;

import java.util.Properties;

public class HibernateSessionFactory {

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
            sessionFactory.close();
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
        properties.put(Environment.URL, "jdbc:hsqldb:mem:mtsa");
        properties.put(Environment.USER, "sa");
        properties.put(Environment.PASS, "");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");
        properties.put(Environment.HBM2DDL_AUTO, "create-drop");
        return properties;
    }
}
