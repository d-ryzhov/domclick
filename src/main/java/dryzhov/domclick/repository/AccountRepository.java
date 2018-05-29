package dryzhov.domclick.repository;

import dryzhov.domclick.domain.Account;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;

public interface AccountRepository extends CrudRepository<Account, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Account findByClientUsernameAndAccount(String username, String account);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Account getByClientUsernameAndAccount(String username, String account);

    void deleteByClientUsernameAndAccount(String username, String account);
}
