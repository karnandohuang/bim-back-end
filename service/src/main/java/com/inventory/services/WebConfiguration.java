package com.inventory.services;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.inventory.models")
@EnableJpaRepositories(basePackages = "com.inventory.repositories", entityManagerFactoryRef = "emf")
public class WebConfiguration {

}
