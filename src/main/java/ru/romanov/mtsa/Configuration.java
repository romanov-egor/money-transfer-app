package ru.romanov.mtsa;

import ru.romanov.mtsa.servlet.HelloWorldServlet;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class Configuration extends Application{

    public Configuration( ) {}

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>( );
        classes.add(HelloWorldServlet.class );
        return classes;
    }
}
