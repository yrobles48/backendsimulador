 package com.prestaclub.prestaclub_backend.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@MapperScan(
    basePackages = "com.prestaclub.prestaclub_backend.persistence.antiguo.mapper",
    sqlSessionFactoryRef = "antiguoSqlSessionFactory"
) 
public class MyBatisConfig {

        @Bean("antiguoSqlSessionFactory")
        public SqlSessionFactory sqlSessionFactory(@Qualifier("antiguoDataSource") DataSource antiguoDS) throws Exception {
            SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
            sqlSessionFactoryBean.setDataSource(antiguoDS);
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
            return sqlSessionFactoryBean.getObject();
        }
    
 }
