package com.kwaski.auth.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(
        basePackages = {"com.kwaski.auth.repository.postgres"},
        sqlSessionFactoryRef = "postgresSqlSessionFactory")
public class PostgresDataSourceConfig {

    @Primary
    @Bean(name = {"postgresDataSource"})
    public DataSource dataSource(
            @Qualifier("postgresHikariConfig") HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    @Bean(name = {"postgresHikariConfig"})
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.postgres.hikari")
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Primary
    @Bean(name = {"postgresTxManager"})
    public PlatformTransactionManager txManager(
            @Qualifier("postgresDataSource")DataSource dataSource) {

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    @Primary
    @Bean(name = {"postgresSqlSessionFactory"})
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("postgresDataSource") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setConfigLocation(new ClassPathResource("/mybatis-config.xml"));
        return sqlSessionFactory.getObject();
    }
}
