package bank.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(double amount) {
        super("Montant invalide : " + amount);
    }
}
