package com.dcrissman.axonplayground;

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
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(datasource);
        em.setPackagesToScan(
                new String[]{PACKAGE_TO_SCAN});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(generateDdl);
        vendorAdapter.setShowSql(showSql);
        vendorAdapter.setDatabase(dialect);
        em.setJpaVendorAdapter(vendorAdapter);

        return em;
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

}
