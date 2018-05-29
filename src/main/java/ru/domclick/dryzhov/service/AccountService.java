package ru.domclick.dryzhov.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.domclick.dryzhov.domain.Account;
import ru.domclick.dryzhov.domain.Client;
import ru.domclick.dryzhov.repository.AccountRepository;
import ru.domclick.dryzhov.repository.ClientRepository;

import java.math.BigDecimal;

/**
 * Service for operation with client's account
 *
 * @author dryzhov
 */
@Slf4j
@Service
@Transactional
public class AccountService {
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    public AccountService(ClientRepository clientRepository, AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    public void createAccount(String username, String account) {
        log.info("Creating new account for user {}: {}", username, account);

        Client client = clientRepository.findOne(username);
        Assert.notNull(client, "Client has not been found");

        client.addAccount(new Account(account));

        clientRepository.save(client);
    }

    public void deleteAccount(String username, String account) {
        log.info("Deleting account for user {}: {}", username, account);
        accountRepository.deleteByClientUsernameAndAccount(username, account);
    }

    public BigDecimal getAccountMoney(String username, String account) {
        Account accnt = accountRepository.getByClientUsernameAndAccount(username, account);
        Assert.notNull(accnt, "Client or Account has not been found");

        return accnt.getMoney();
    }

    public void deposit(String username, String account, BigDecimal money) {
        log.info("Adding {} money to account {} of user {}", money, account, username);
        Account acnt = accountRepository.findByClientUsernameAndAccount(username, account);
        Assert.notNull(acnt, "Client or Account has not been found");

        BigDecimal sum = acnt.getMoney();
        sum = sum.add(money);
        acnt.setMoney(sum);

        accountRepository.save(acnt);
    }

    public void withdraw(String username, String account, BigDecimal money) {
        log.info("Removing {} money from account {} of user {}", money, account, username);
        Account acnt = accountRepository.findByClientUsernameAndAccount(username, account);
        Assert.notNull(acnt, "Client or Account has not been found");

        BigDecimal sum = acnt.getMoney();
        sum = sum.subtract(money);
        acnt.setMoney(sum);

        accountRepository.save(acnt);
    }

    public void transfer(String usernameFrom, String accountFrom, String usernameTo, String accountTo, BigDecimal money) {
        log.info("Transferring {} money from account {} of user {} to account {} of user {}", money, accountFrom, usernameFrom, accountTo, usernameTo);
        //H2 locks for update entire table but this is for common case
        Account acntFrom, acntTo;
        Assert.isTrue(!accountFrom.equals(accountTo), "Source and target accounts must be different");
        if (accountTo.compareTo(accountFrom) > 0) {
            acntFrom = accountRepository.findByClientUsernameAndAccount(usernameFrom, accountFrom);
            acntTo = accountRepository.findByClientUsernameAndAccount(usernameTo, accountTo);
        } else {
            acntTo = accountRepository.findByClientUsernameAndAccount(usernameTo, accountTo);
            acntFrom = accountRepository.findByClientUsernameAndAccount(usernameFrom, accountFrom);
        }
        Assert.notNull(acntFrom, "Source Client or Account has not been found");
        Assert.notNull(acntTo, "Target Client or Account has not been found");

        BigDecimal sumFrom = acntFrom.getMoney();
        BigDecimal sumTo = acntTo.getMoney();
        sumFrom = sumFrom.subtract(money);
        Assert.isTrue(sumFrom.compareTo(BigDecimal.ZERO) >= 0, "Amount of money can not be less then zero");
        sumTo = sumTo.add(money);
        acntFrom.setMoney(sumFrom);
        acntTo.setMoney(sumTo);

        accountRepository.save(acntFrom);
        accountRepository.save(acntTo);
    }
}
