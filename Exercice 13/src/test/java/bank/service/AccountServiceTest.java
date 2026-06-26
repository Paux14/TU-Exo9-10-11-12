package bank.service;

import bank.exception.*;
import bank.model.Account;
import bank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    private Account alice;

    @BeforeEach
    void setUp() {
        alice = new Account("ACC-001", "Alice");
        alice.setBalance(500.0);
    }

    // --- Création ---

    @Test
    void createAccount_success() {
        when(repository.existsByNumber("ACC-001")).thenReturn(false);

        Account result = service.createAccount("ACC-001", "Alice");

        assertThat(result.getNumber()).isEqualTo("ACC-001");
        assertThat(result.getOwner()).isEqualTo("Alice");
        assertThat(result.getBalance()).isEqualTo(0.0);
        verify(repository).save(result);
    }

    @Test
    void createAccount_duplicateNumber_throwsException() {
        when(repository.existsByNumber("ACC-001")).thenReturn(true);

        assertThatThrownBy(() -> service.createAccount("ACC-001", "Alice"))
                .isInstanceOf(AccountAlreadyExistsException.class);
    }

    // --- Consultation ---

    @Test
    void getAccount_existingAccount_returnsAccount() {
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));

        Account result = service.getAccount("ACC-001");

        assertThat(result).isEqualTo(alice);
    }

    @Test
    void getAccount_unknownNumber_throwsException() {
        when(repository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAccount("UNKNOWN"))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void getAllAccounts_returnsAllAccounts() {
        when(repository.findAll()).thenReturn(List.of(alice));

        List<Account> result = service.getAllAccounts();

        assertThat(result).containsExactly(alice);
    }

    // --- Dépôt ---

    @Test
    void deposit_validAmount_updatesBalance() {
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));

        Account result = service.deposit("ACC-001", 200.0);

        assertThat(result.getBalance()).isEqualTo(700.0);
    }

    @Test
    void deposit_zeroAmount_throwsException() {
        assertThatThrownBy(() -> service.deposit("ACC-001", 0))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void deposit_negativeAmount_throwsException() {
        assertThatThrownBy(() -> service.deposit("ACC-001", -50))
                .isInstanceOf(InvalidAmountException.class);
    }

    // --- Retrait ---

    @Test
    void withdraw_validAmount_updatesBalance() {
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));

        Account result = service.withdraw("ACC-001", 100.0);

        assertThat(result.getBalance()).isEqualTo(400.0);
    }

    @Test
    void withdraw_zeroAmount_throwsException() {
        assertThatThrownBy(() -> service.withdraw("ACC-001", 0))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void withdraw_negativeAmount_throwsException() {
        assertThatThrownBy(() -> service.withdraw("ACC-001", -10))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void withdraw_insufficientFunds_throwsException() {
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> service.withdraw("ACC-001", 1000.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    // --- Virement ---

    @Test
    void transfer_validAmount_updatesBothBalances() {
        Account bob = new Account("ACC-002", "Bob");
        bob.setBalance(100.0);
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));
        when(repository.findByNumber("ACC-002")).thenReturn(Optional.of(bob));

        service.transfer("ACC-001", "ACC-002", 200.0);

        assertThat(alice.getBalance()).isEqualTo(300.0);
        assertThat(bob.getBalance()).isEqualTo(300.0);
    }

    @Test
    void transfer_zeroAmount_throwsException() {
        assertThatThrownBy(() -> service.transfer("ACC-001", "ACC-002", 0))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void transfer_negativeAmount_throwsException() {
        assertThatThrownBy(() -> service.transfer("ACC-001", "ACC-002", -100))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        Account bob = new Account("ACC-002", "Bob");
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));
        when(repository.findByNumber("ACC-002")).thenReturn(Optional.of(bob));

        assertThatThrownBy(() -> service.transfer("ACC-001", "ACC-002", 9999.0))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void transfer_unknownSourceAccount_throwsException() {
        when(repository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transfer("UNKNOWN", "ACC-002", 100.0))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    void transfer_unknownDestinationAccount_throwsException() {
        when(repository.findByNumber("ACC-001")).thenReturn(Optional.of(alice));
        when(repository.findByNumber("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transfer("ACC-001", "UNKNOWN", 100.0))
                .isInstanceOf(AccountNotFoundException.class);
    }
}
