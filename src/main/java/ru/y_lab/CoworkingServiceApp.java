package ru.y_lab;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ru.y_lab.config.OpenApiConfiguration;
import ru.y_lab.config.WebConfig;

/**
 * The CoworkingServiceApp class serves as the main application entry point for managing coworking resources and bookings.
 */
public class CoworkingServiceApp implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("ru.y_lab.config");
        context.register(WebConfig.class);
        context.register(OpenApiConfiguration.class);
        servletContext.addListener(new ContextLoaderListener(context));
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}
