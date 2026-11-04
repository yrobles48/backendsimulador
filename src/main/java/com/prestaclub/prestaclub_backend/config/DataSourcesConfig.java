package com.prestaclub.prestaclub_backend.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; 

@Configuration
public class DataSourcesConfig {
 
    @Bean("nuevoProperties")
    @ConfigurationProperties(prefix = "spring.datasource.nuevo")
    public DataSourceProperties getNuevoProperties() {
        return new DataSourceProperties();
    }

 
    @Bean("nuevoDataSource")
    public DataSource getNuevoDataSource() {
        return getNuevoProperties().initializeDataSourceBuilder().build();
    }

    @Bean("antiguoProperties")
    @ConfigurationProperties(prefix = "spring.datasource.antiguo")
    public DataSourceProperties getAntiguoProperties() {
        return new DataSourceProperties();
    }

    @Bean("antiguoDataSource")
    public DataSource getAntiguoDataSource() {
        return getAntiguoProperties().initializeDataSourceBuilder().build();
    }
}
