package com.dcrissman.axonplayground;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = DatabaseConfiguration.PACKAGE_TO_SCAN,
        entityManagerFactoryRef = DatabaseConfiguration.ENTITY_MANAGER,
        transactionManagerRef = DatabaseConfiguration.TRANSACTION_MANAGER
        )
public class DatabaseConfiguration {

    public static final String DATASOURCE = "datasource";
    public static final String ENTITY_MANAGER = "entityManager";
    public static final String TRANSACTION_MANAGER = "transactionManager";
    public static final String TRANSACTION_TEMPLATE = "transactionTemplate";

    public static final String PACKAGE_TO_SCAN = "org.axonframework.eventsourcing.eventstore.jpa";

    private static LocalContainerEntityManagerFactoryBean createEntityManager(
            DataSource datasource,
            DataSourceOptions options,
            String[] packagesToScan) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(datasource);
        em.setPackagesToScan(packagesToScan);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(options.isGenerateDdl());
        vendorAdapter.setShowSql(options.isShowSql());
        vendorAdapter.setDatabase(options.getDialect());
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        if (null != options.getHbm2ddl()) {
            properties.put("hibernate.hbm2ddl.auto", options.getHbm2ddl());
        }

        if (!properties.isEmpty()) {
            em.setJpaPropertyMap(properties);
        }

        return em;
    }

    @Bean(name = DATASOURCE)
    @ConfigurationProperties("datasource")
    @Primary
    public DataSource datasourceTabbyCatApps() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = ENTITY_MANAGER)
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerTabbyCatApps(
            @Qualifier(DATASOURCE) DataSource datasource,
            @Value("${datasource.generateDdl:false}") boolean generateDdl,
            @Value("${datasource.showSql:false}") boolean showSql,
            @Value("${datasource.dialect}") Database dialect) {
        return createEntityManager(datasource, new DataSourceOptions(generateDdl, showSql, dialect, "create-drop"), new String[] {PACKAGE_TO_SCAN});
    }

    @Bean(TRANSACTION_MANAGER)
    @Primary
    public PlatformTransactionManager transactionManagerTabbyCatApps(
            @Qualifier(ENTITY_MANAGER) LocalContainerEntityManagerFactoryBean entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager.getObject());
        return transactionManager;
    }

    @Bean(TRANSACTION_TEMPLATE)
    @Primary
    public TransactionTemplate transactionTemplateTabbyCatApps(
            @Qualifier(TRANSACTION_MANAGER) PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataSourceOptions {
        private boolean generateDdl;
        private boolean showSql;
        private Database dialect;
        private String hbm2ddl;
    }

}
