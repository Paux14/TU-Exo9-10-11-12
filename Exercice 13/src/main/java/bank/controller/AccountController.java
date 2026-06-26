package bank.controller;

import bank.model.Account;
import bank.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@RequestBody CreateAccountRequest request) {
        return service.createAccount(request.number(), request.owner());
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return service.getAllAccounts();
    }

    @GetMapping("/{number}")
    public Account getAccount(@PathVariable String number) {
        return service.getAccount(number);
    }

    @PostMapping("/{number}/deposit")
    public Account deposit(@PathVariable String number, @RequestBody AmountRequest request) {
        return service.deposit(number, request.amount());
    }

    @PostMapping("/{number}/withdraw")
    public Account withdraw(@PathVariable String number, @RequestBody AmountRequest request) {
        return service.withdraw(number, request.amount());
    }

    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody TransferRequest request) {
        service.transfer(request.fromNumber(), request.toNumber(), request.amount());
    }
}
