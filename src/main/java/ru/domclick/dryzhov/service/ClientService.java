package ru.domclick.dryzhov.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.domclick.dryzhov.domain.Client;
import ru.domclick.dryzhov.repository.ClientRepository;

/**
 * Service for creating/deleting clients etc
 *
 * @author dryzhov
 */
@Service
@Transactional
public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(String username) {
        Client client = new Client(username);
        clientRepository.save(client);
    }

    public void deleteClient(String username) {
        clientRepository.delete(username);
    }
}
