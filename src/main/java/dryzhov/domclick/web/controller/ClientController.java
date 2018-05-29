package dryzhov.domclick.web.controller;

import dryzhov.domclick.service.ClientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/{username}")
    public void create(@PathVariable("username") String username) {
        clientService.createClient(username);
    }

    @DeleteMapping("/{username}")
    public void delete(@PathVariable("username") String username) {
        clientService.deleteClient(username);
    }
}
