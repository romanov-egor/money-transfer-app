package ru.romanov.mtsa;

import org.glassfish.jersey.jackson.JacksonFeature;
import ru.romanov.mtsa.servlet.AccountServlet;
import ru.romanov.mtsa.servlet.TransferServlet;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class Configuration extends Application{

    public Configuration( ) {}

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>( );
        classes.add(JacksonFeature.class);
        classes.add(AccountServlet.class);
        classes.add(TransferServlet.class);
        return classes;
    }
}
