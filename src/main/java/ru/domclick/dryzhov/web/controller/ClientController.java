package ru.domclick.dryzhov.web.controller;

import org.springframework.web.bind.annotation.*;
import ru.domclick.dryzhov.service.AccountService;
import ru.domclick.dryzhov.service.ClientService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;
    private final AccountService accountService;

    public ClientController(ClientService clientService, AccountService accountService) {
        this.clientService = clientService;
        this.accountService = accountService;
    }

    @PostMapping("/{username}")
    public void create(@PathVariable("username") String username) {
        clientService.createClient(username);
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable("username") String username) {
        clientService.deleteClient(username);
    }

    @GetMapping("/{username}/{account}")
    public BigDecimal getAccountMoney(@PathVariable String username, @PathVariable String account) {
        return accountService.getAccountMoney(username, account);
    }

}
