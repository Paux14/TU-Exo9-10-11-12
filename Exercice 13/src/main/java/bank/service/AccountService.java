package bank.service;

import bank.exception.*;
import bank.model.Account;
import bank.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    public Account createAccount(String number, String owner) {
        if (repository.existsByNumber(number)) {
            throw new AccountAlreadyExistsException(number);
        }
        Account account = new Account(number, owner);
        repository.save(account);
        return account;
    }

    public Account getAccount(String number) {
        return repository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException(number));
    }

    public List<Account> getAllAccounts() {
        return repository.findAll();
    }

    public Account deposit(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        Account account = getAccount(number);
        account.setBalance(account.getBalance() + amount);
        return account;
    }

    public Account withdraw(String number, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        Account account = getAccount(number);
        if (account.getBalance() < amount) {
            throw new InsufficientFundsException(number, account.getBalance(), amount);
        }
        account.setBalance(account.getBalance() - amount);
        return account;
    }

    public void transfer(String fromNumber, String toNumber, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException(amount);
        }
        Account from = getAccount(fromNumber);
        Account to = getAccount(toNumber);
        if (from.getBalance() < amount) {
            throw new InsufficientFundsException(fromNumber, from.getBalance(), amount);
        }
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
    }
}
