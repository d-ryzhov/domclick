package ru.domclick.dryzhov.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.domclick.dryzhov.service.AccountService;
import ru.domclick.dryzhov.web.controller.dto.Deposit;
import ru.domclick.dryzhov.web.controller.dto.Transfer;
import ru.domclick.dryzhov.web.controller.dto.Withdraw;

import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/deposit")
    public void deposit(@RequestBody @Valid Deposit deposit) {
        accountService.deposit(deposit.getUsername(), deposit.getAccount(), deposit.getMoney());
    }

    @PostMapping("/withdraw")
    public void withdraw(@RequestBody @Valid Withdraw withdraw) {
        accountService.withdraw(withdraw.getUsername(), withdraw.getAccount(), withdraw.getMoney());
    }

    @PostMapping("/transfer")
    public void transfer(@RequestBody @Valid Transfer transfer) {
        accountService.transfer(transfer.getUsernameFrom(), transfer.getAccountFrom(),
                transfer.getUsernameTo(), transfer.getAccountTo(), transfer.getMoney());
    }
}
