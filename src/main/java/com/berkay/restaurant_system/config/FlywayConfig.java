package com.berkay.restaurant_system.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This project's Spring Boot distribution does not ship FlywayAutoConfiguration
// (spring-boot-autoconfigure has no org.springframework.boot.autoconfigure.flyway
// package at all), so migrations are wired up manually here, replicating the same
// "run Flyway before the JPA EntityManagerFactory is built" behavior Spring Boot
// normally provides out of the box.
@Configuration
public class FlywayConfig {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource,
            @Value("${spring.flyway.baseline-on-migrate:true}") boolean baselineOnMigrate,
            @Value("${spring.flyway.baseline-version:1}") String baselineVersion) {
        return Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(baselineVersion)
                .load();
    }

    @Bean
    public static BeanFactoryPostProcessor flywayEntityManagerFactoryDependsOnPostProcessor() {
        return beanFactory -> {
            if (beanFactory.containsBeanDefinition("entityManagerFactory")) {
                beanFactory.getBeanDefinition("entityManagerFactory").setDependsOn("flyway");
            }
        };
    }
}
