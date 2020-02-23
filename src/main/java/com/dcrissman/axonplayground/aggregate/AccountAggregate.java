package com.dcrissman.axonplayground.aggregate;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dcrissman.axonplayground.Status;
import com.dcrissman.axonplayground.command.CreateAccountCommand;
import com.dcrissman.axonplayground.command.CreditMoneyCommand;
import com.dcrissman.axonplayground.command.DebitMoneyCommand;
import com.dcrissman.axonplayground.event.AccountActivatedEvent;
import com.dcrissman.axonplayground.event.AccountCreatedEvent;
import com.dcrissman.axonplayground.event.AccountHeldEvent;
import com.dcrissman.axonplayground.event.MoneyCreditedEvent;
import com.dcrissman.axonplayground.event.MoneyDebitedEvent;
import com.dcrissman.axonplayground.service.AccountQueryService;

@Aggregate
public class AccountAggregate {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountAggregate.class);
    
    @AggregateIdentifier
    private String id;
    private double accountBalance;
    private String currency;
    private String status;

    public AccountAggregate() {
        LOGGER.info("init AccountAggregate");
    }

    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        this();
        AggregateLifecycle.apply(
                new AccountCreatedEvent(createAccountCommand.id, createAccountCommand.accountBalance, createAccountCommand.currency));
    }

    @EventSourcingHandler
    protected void on(AccountCreatedEvent accountCreatedEvent) {
        id = accountCreatedEvent.id;
        accountBalance = accountCreatedEvent.accountBalance;
        currency = accountCreatedEvent.currency;
        status = String.valueOf(Status.CREATED);

        AggregateLifecycle.apply(
                new AccountActivatedEvent(id, Status.ACTIVATED));
    }

    @EventSourcingHandler
    protected void on(AccountActivatedEvent accountActivatedEvent) {
        status = String.valueOf(accountActivatedEvent.status);
    }

    @CommandHandler
    protected void on(CreditMoneyCommand creditMoneyCommand) {
        AggregateLifecycle.apply(
                new MoneyCreditedEvent(creditMoneyCommand.id, creditMoneyCommand.creditAmount, creditMoneyCommand.currency));
    }

    @EventSourcingHandler
    protected void on(MoneyCreditedEvent moneyCreditedEvent) {
        if (accountBalance < 0 & (accountBalance + moneyCreditedEvent.creditAmount) >= 0) {
            AggregateLifecycle.apply(
                    new AccountActivatedEvent(id, Status.ACTIVATED));
        }
        accountBalance += moneyCreditedEvent.creditAmount;
    }

    @CommandHandler
    protected void on(DebitMoneyCommand debitMoneyCommand) {
        AggregateLifecycle.apply(
                new MoneyDebitedEvent(debitMoneyCommand.id, debitMoneyCommand.debitAmount, debitMoneyCommand.currency));
    }

    @EventSourcingHandler
    protected void on(MoneyDebitedEvent moneyDebitedEvent) {
        if (accountBalance >= 0 & (accountBalance - moneyDebitedEvent.debitAmount) < 0) {
            AggregateLifecycle.apply(new AccountHeldEvent(id, Status.HOLD));
        }
        accountBalance -= moneyDebitedEvent.debitAmount;
    }

    @EventSourcingHandler
    protected void on(AccountHeldEvent accountHeldEvent) {
        status = String.valueOf(accountHeldEvent.status);
    }

}
