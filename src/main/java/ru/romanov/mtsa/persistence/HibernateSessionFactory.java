package ru.romanov.mtsa.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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

    private static SessionFactory initializeSessionFactory() {
        return new Configuration().buildSessionFactory();
    }

    public static void closeSession() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
