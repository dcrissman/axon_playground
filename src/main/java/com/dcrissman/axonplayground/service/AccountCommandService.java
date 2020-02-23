package com.dcrissman.axonplayground.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dcrissman.axonplayground.command.CreateAccountCommand;
import com.dcrissman.axonplayground.command.CreditMoneyCommand;
import com.dcrissman.axonplayground.command.DebitMoneyCommand;
import com.dcrissman.axonplayground.model.AccountCreateDTO;
import com.dcrissman.axonplayground.model.MoneyCreditDTO;
import com.dcrissman.axonplayground.model.MoneyDebitDTO;

@Service
public class AccountCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCommandService.class);

    private final CommandGateway commandGateway;

    public AccountCommandService(CommandGateway commandGateway) {
        LOGGER.info("init AccountCommandService");
        this.commandGateway = commandGateway;
    }

    public CompletableFuture<String> createAccount(AccountCreateDTO accountCreateDTO) {
        return commandGateway.send(new CreateAccountCommand(UUID.randomUUID().toString(), accountCreateDTO.getStartingBalance(), accountCreateDTO.getCurrency()));
    }

    public CompletableFuture<String> creditMoneyToAccount(String accountNumber, MoneyCreditDTO moneyCreditDTO) {
        return commandGateway.send(new CreditMoneyCommand(accountNumber, moneyCreditDTO.getCreditAmount(), moneyCreditDTO.getCurrency()));
    }

    public CompletableFuture<String> debitMoneyFromAccount(String accountNumber, MoneyDebitDTO moneyDebitDTO) {
        return commandGateway.send(new DebitMoneyCommand(accountNumber, moneyDebitDTO.getDebitAmount(), moneyDebitDTO.getCurrency()));
    }
}
