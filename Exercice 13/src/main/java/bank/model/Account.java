package bank.model;

public class Account {

    private final String number;
    private final String owner;
    private double balance;

    public Account(String number, String owner) {
        this.number = number;
        this.owner = owner;
        this.balance = 0.0;
    }

    public String getNumber() { return number; }
    public String getOwner() { return owner; }
    public double getBalance() { return balance; }

    public void setBalance(double balance) { this.balance = balance; }
}
