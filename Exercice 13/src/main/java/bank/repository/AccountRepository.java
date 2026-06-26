package bank.repository;

import bank.model.Account;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountRepository {

    private final Map<String, Account> store = new HashMap<>();

    public void save(Account account) {
        store.put(account.getNumber(), account);
    }

    public Optional<Account> findByNumber(String number) {
        return Optional.ofNullable(store.get(number));
    }

    public List<Account> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean existsByNumber(String number) {
        return store.containsKey(number);
    }
}
