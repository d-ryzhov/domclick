package dryzhov.domclick.web.controller;

import dryzhov.domclick.service.AccountService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public BigDecimal getAccountMoney(@RequestParam String username, @RequestParam String account) {
        return accountService.getAccountMoney(username, account);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestParam String usernameFrom, @RequestParam String accountFrom,
                         @RequestParam String usernameTo, @RequestParam String accountTo,
                         @RequestParam BigDecimal money) {
        accountService.transfer(usernameFrom, accountFrom, usernameTo, accountTo, money);
    }
}
