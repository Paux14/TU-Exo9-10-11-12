package bank.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String number) {
        super("Compte introuvable : " + number);
    }
}
