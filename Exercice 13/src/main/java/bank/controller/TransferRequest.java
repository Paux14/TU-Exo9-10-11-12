package bank.controller;

public record TransferRequest(String fromNumber, String toNumber, double amount) {}
