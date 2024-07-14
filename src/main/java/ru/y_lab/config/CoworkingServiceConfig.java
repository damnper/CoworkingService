package ru.y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import ru.y_lab.aspect.UserAuditAspect;

@Configuration
@EnableAspectJAutoProxy
public class CoworkingServiceConfig {

    @Bean
    public UserAuditAspect userAuditAspect() {
        return new UserAuditAspect();
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("application.properties"));
        return propertySourcesPlaceholderConfigurer;
    }
}
