package ru.domclick.dryzhov.repository;

import org.springframework.data.repository.CrudRepository;
import ru.domclick.dryzhov.domain.Client;

public interface ClientRepository extends CrudRepository<Client, String> {
    @Override
    Client findOne(String id);
}
