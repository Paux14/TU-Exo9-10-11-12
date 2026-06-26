package bank.bdd.steps;

import bank.exception.InsufficientFundsException;
import bank.model.Account;
import bank.repository.AccountRepository;
import bank.service.AccountService;
import io.cucumber.java.Before;
import io.cucumber.java.fr.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountStepDefinitions {

    private AccountService service;
    private AccountRepository repository;
    private Exception thrownException;

    @Before
    public void setUp() {
        repository = new AccountRepository();
        service = new AccountService(repository);
    }

    @Quand("je crée un compte avec le numéro {string} et le titulaire {string}")
    public void createAccount(String number, String owner) {
        service.createAccount(number, owner);
    }

    @Alors("le compte {string} existe avec un solde de {double}")
    public void accountExistsWithBalance(String number, double balance) {
        Account account = service.getAccount(number);
        assertThat(account.getBalance()).isEqualTo(balance);
    }

    @Etantdonné("un compte {string} appartenant à {string} avec un solde de {double}")
    public void accountWithBalance(String number, String owner, double balance) {
        service.createAccount(number, owner);
        if (balance > 0) {
            service.deposit(number, balance);
        }
    }

    @Et("un compte {string} appartenant à {string} avec un solde de {double}")
    public void andAccountWithBalance(String number, String owner, double balance) {
        accountWithBalance(number, owner, balance);
    }

    @Quand("je dépose {double} sur le compte {string}")
    public void deposit(double amount, String number) {
        service.deposit(number, amount);
    }

    @Alors("le solde du compte {string} est de {double}")
    public void balanceIs(String number, double balance) {
        assertThat(service.getAccount(number).getBalance()).isEqualTo(balance);
    }

    @Et("le solde du compte {string} est de {double}")
    public void andBalanceIs(String number, double balance) {
        balanceIs(number, balance);
    }

    @Quand("je retire {double} du compte {string}")
    public void withdraw(double amount, String number) {
        service.withdraw(number, amount);
    }

    @Quand("je tente de retirer {double} du compte {string}")
    public void attemptWithdraw(double amount, String number) {
        try {
            service.withdraw(number, amount);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Alors("une erreur de fonds insuffisants est levée")
    public void insufficientFundsError() {
        assertThat(thrownException).isInstanceOf(InsufficientFundsException.class);
    }

    @Quand("je vire {double} du compte {string} vers le compte {string}")
    public void transfer(double amount, String from, String to) {
        service.transfer(from, to, amount);
    }

    @Quand("je tente de virer {double} du compte {string} vers le compte {string}")
    public void attemptTransfer(double amount, String from, String to) {
        try {
            service.transfer(from, to, amount);
        } catch (Exception e) {
            thrownException = e;
        }
    }
}
