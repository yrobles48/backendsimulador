package com.prestaclub.prestaclub_backend.config;

import java.util.*;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.prestaclub.prestaclub_backend.persistence.nuevo.repository",
    entityManagerFactoryRef = "nuevoEMF",
    transactionManagerRef = "nuevoTrxM"
)
@EnableTransactionManagement
public class NuevoJpaConfig {

    @Bean
    public EntityManagerFactoryBuilder getEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(),null);
    }

    @Bean("nuevoEMF")
    public LocalContainerEntityManagerFactoryBean nuevoEntityManagerFactory(
            @Qualifier("nuevoDataSource") DataSource nuevoDS,
            EntityManagerFactoryBuilder builder
            ) {
            Map<String, String> additionalProps = new HashMap<>();
            additionalProps.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); 
            additionalProps.put("hibernate.hbm2ddl.auto", "update"); 
            additionalProps.put("hibernate.show_sql", "true"); 
            return builder 
                .dataSource(nuevoDS)
                .persistenceUnit("nuevoPersistenceUnit")
                .properties(additionalProps)
                .packages("com.prestaclub.prestaclub_backend.persistence.nuevo.model")
                .build();  
    }

    @Bean("nuevoTrxM")
    public JpaTransactionManager getNuevoTrxManager(@Qualifier("nuevoEMF") LocalContainerEntityManagerFactoryBean nuevoEMF) {
        return new JpaTransactionManager(Objects.requireNonNull(nuevoEMF.getObject()));
    }
}
