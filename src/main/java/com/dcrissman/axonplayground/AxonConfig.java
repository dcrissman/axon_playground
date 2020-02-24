package com.dcrissman.axonplayground;

import javax.persistence.EntityManager;

import org.axonframework.common.jdbc.PersistenceExceptionResolver;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.serialization.Serializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AxonConfig {

    @Bean
    public EntityManagerProvider entityManagerProvider(
            @Qualifier(DatabaseConfiguration.ENTITY_MANAGER) EntityManager entityManager) {
        return new EntityManagerProvider() {

            @Override
            public EntityManager getEntityManager() {
                return entityManager;
            }

        };
    }

    @Bean
    public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
        return EmbeddedEventStore.builder()
                .storageEngine(storageEngine)
                .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
                .build();
    }

    // EventStorageEngine implementation that uses JDBC to store and fetch events.
    @Bean
    public EventStorageEngine eventStorageEngine(
            Serializer defaultSerializer,
            PersistenceExceptionResolver persistenceExceptionResolver,
            @Qualifier("eventSerializer") Serializer eventSerializer,
            AxonConfiguration configuration,
            EntityManagerProvider entityManagerProvider,
            @Qualifier(DatabaseConfiguration.TRANSACTION_MANAGER) PlatformTransactionManager transactionManager) {
        return JpaEventStorageEngine.builder()
                .snapshotSerializer(defaultSerializer)
                .upcasterChain(configuration.upcasterChain())
                .persistenceExceptionResolver(persistenceExceptionResolver)
                .eventSerializer(eventSerializer)
                .entityManagerProvider(entityManagerProvider)
                .transactionManager(new SpringTransactionManager(transactionManager))
                .build();
    }

}
