package ru.domclick.dryzhov.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.domclick.dryzhov.domain.Client;
import ru.domclick.dryzhov.repository.ClientRepository;

/**
 * Service for creating/deleting clients etc
 *
 * @author dryzhov
 */
@Slf4j
@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(String username) {
        log.info("Creating new client {}", username);
        Client client = new Client(username);
        clientRepository.save(client);
    }

    public void deleteClient(String username) {
        log.info("Deleting client {}", username);
        clientRepository.delete(username);
    }
}
