package ru.romanov.mta;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static final Logger log = Logger.getLogger(Main.class.getName());

    public static final String CONTEXT_PATH = "";
    public static final String APP_BASE = ".";
    public static final Optional<String> PORT = Optional.ofNullable(System.getenv("PORT"));

    public static void main(String[] args) throws Exception {
        int port = Integer.valueOf(PORT.orElse("8080"));

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getHost().setAppBase(APP_BASE);
        Context context = tomcat.addWebapp(CONTEXT_PATH, APP_BASE);

        tomcat.addServlet(context, "mta-servlet",
                new ServletContainer(new ResourceConfig(new WebAppConfig().getClasses())));
        context.addServletMappingDecoded("/*", "mta-servlet");

        try {
            tomcat.start();
            log.info(String.format("Tomcat started at port %d", port));
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            log.log(Level.SEVERE, String.format("Failed to start Tomcat at port %d", port), e);
        } finally {
            tomcat.destroy();
        }
    }
}
