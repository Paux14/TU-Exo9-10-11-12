package bank.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String number, double balance, double amount) {
        super("Fonds insuffisants sur le compte " + number + " (solde=" + balance + ", demande=" + amount + ")");
    }
}
