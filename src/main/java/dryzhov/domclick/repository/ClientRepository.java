package dryzhov.domclick.repository;

import dryzhov.domclick.domain.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
    @Override
    Client findOne(String id);
}
