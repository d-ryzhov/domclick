package dryzhov.domclick.service;

import dryzhov.domclick.domain.Account;
import dryzhov.domclick.domain.Client;
import dryzhov.domclick.repository.AccountRepository;
import dryzhov.domclick.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;

/**
 * Service for operation with client's account
 *
 * @author dryzhov
 */
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
        Client client = clientRepository.findOne(username);
        Assert.notNull(client, "Client has not been found");

        client.addAccount(new Account(account));

        clientRepository.save(client);
    }

    public void deleteAccount(String username, String account) {
        accountRepository.deleteByClientUsernameAndAccount(username, account);
    }

    public BigDecimal getAccountMoney(String username, String account) {
        Account accnt = accountRepository.getByClientUsernameAndAccount(username, account);
        Assert.notNull(accnt, "Client or Account has not been found");

        return accnt.getMoney();
    }

    public void deposit(String username, String account, BigDecimal money) {
        Account acnt = accountRepository.findByClientUsernameAndAccount(username, account);
        Assert.notNull(acnt, "Client or Account has not been found");

        BigDecimal sum = acnt.getMoney();
        sum = sum.add(money);
        acnt.setMoney(sum);

        accountRepository.save(acnt);
    }

    public void withdraw(String username, String account, BigDecimal money) {
        Account acnt = accountRepository.findByClientUsernameAndAccount(username, account);
        Assert.notNull(acnt, "Client or Account has not been found");

        BigDecimal sum = acnt.getMoney();
        sum = sum.subtract(money);
        acnt.setMoney(sum);

        accountRepository.save(acnt);
    }

    public void transfer(String usernameFrom, String accountFrom, String usernameTo, String accountTo, BigDecimal money) {
        Account acntFrom = accountRepository.findByClientUsernameAndAccount(usernameFrom, accountFrom);
        Account acntTo = accountRepository.findByClientUsernameAndAccount(usernameTo, accountTo);
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
