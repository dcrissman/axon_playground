package com.dcrissman.axonplayground.service;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryService.class);
    
    private final EventStore eventStore;

    public AccountQueryService(EventStore eventStore) {
        LOGGER.info("init AccountQueryService");
        this.eventStore = eventStore;
    }

    public List<Object> listEventsForAccount(String accountNumber) {
        return eventStore.readEvents(accountNumber).asStream().map(
                s -> s.getPayload()).collect(Collectors.toList());
    }

}
