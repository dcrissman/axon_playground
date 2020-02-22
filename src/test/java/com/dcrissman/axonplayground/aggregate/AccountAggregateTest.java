package com.dcrissman.axonplayground.aggregate;

import java.util.UUID;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.Test;

import com.dcrissman.axonplayground.Status;
import com.dcrissman.axonplayground.command.CreateAccountCommand;
import com.dcrissman.axonplayground.event.AccountActivatedEvent;
import com.dcrissman.axonplayground.event.AccountCreatedEvent;

public class AccountAggregateTest {

    @Test
    public void test() {
        String id = UUID.randomUUID().toString();
        
        FixtureConfiguration<AccountAggregate> fixture = new AggregateTestFixture<>(AccountAggregate.class);
        fixture.givenNoPriorActivity()
            .when(new CreateAccountCommand(id, 7.23, "USD"))
            .expectEvents(new AccountCreatedEvent(id, 7.23, "USD"), new AccountActivatedEvent(id, Status.ACTIVATED));
    }

}
