package ru.y_lab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.y_lab.aspect.UserAuditAspect;

@Configuration
@EnableAspectJAutoProxy
public class CoworkingServiceConfig {

    @Bean
    public UserAuditAspect userAuditAspect() {
        return new UserAuditAspect();
    }
}
