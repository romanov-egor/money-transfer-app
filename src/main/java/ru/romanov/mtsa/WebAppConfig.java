package ru.romanov.mtsa;

import org.glassfish.jersey.jackson.JacksonFeature;
import ru.romanov.mtsa.servlet.AccountServlet;
import ru.romanov.mtsa.servlet.TransferServlet;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Egor Romanov
 */
public class WebAppConfig extends Application{

    public WebAppConfig( ) {}

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>( );
        // JacksonFeature class is necessary to enable producing and consuming JSON
        classes.add(JacksonFeature.class);
        classes.add(AccountServlet.class);
        classes.add(TransferServlet.class);
        return classes;
    }
}
