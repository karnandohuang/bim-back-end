package com.inventory.services;

import com.inventory.models.BaseEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "myAuditorProvider")
@ComponentScan(basePackages = "com.inventory.models")
@EnableJpaRepositories(basePackages = "com.inventory.repositories", entityManagerFactoryRef = "emf")
public class WebConfiguration {

    @Bean
    public AuditorAware<String> myAuditorProvider() {
        return new AuditorAwareImpl();
    }

}
