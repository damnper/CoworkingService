package ru.y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import ru.y_lab.aspect.UserAuditAspect;


/**
 * Configuration class for the coworking service application.
 * This class configures various beans and enables AspectJ auto proxying.
 */
@Configuration
@EnableAspectJAutoProxy
public class CoworkingServiceConfig {

    /**
     * Creates and returns a {@link UserAuditAspect} bean.
     * The aspect is used to audit user actions within the application.
     *
     * @return a new instance of {@link UserAuditAspect}
     */
    @Bean
    public UserAuditAspect userAuditAspect() {
        return new UserAuditAspect();
    }

    /**
     * Creates and returns a {@link PropertySourcesPlaceholderConfigurer} bean.
     * This bean is used to resolve ${...} placeholders within bean definition property values
     * and @Value annotations against the current Spring {@link org.springframework.core.env.Environment}.
     *
     * @return a configured instance of {@link PropertySourcesPlaceholderConfigurer}
     */
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("application.yml"));
        return propertySourcesPlaceholderConfigurer;
    }
}
