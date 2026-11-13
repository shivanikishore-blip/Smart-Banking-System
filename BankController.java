package com.smartbank.controller;

import com.smartbank.dto.Dtos.*;
import com.smartbank.model.Account;
import com.smartbank.model.BankTransaction;
import com.smartbank.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BankController {

    private final BankService service;

    public BankController(BankService service) {
        this.service = service;
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> create(@Valid @RequestBody CreateAccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createAccount(req));
    }

    @PostMapping("/auth/login")
    public Account login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @GetMapping("/accounts")
    public List<Account> all() {
        return service.allAccounts();
    }

    @GetMapping("/accounts/{accNo}")
    public Account get(@PathVariable String accNo) {
        return service.getAccount(accNo);
    }

    @PostMapping("/accounts/{accNo}/deposit")
    public Account deposit(@PathVariable String accNo, @Valid @RequestBody AmountRequest req) {
        return service.deposit(accNo, req.amount());
    }

    @PostMapping("/accounts/{accNo}/withdraw")
    public Account withdraw(@PathVariable String accNo, @Valid @RequestBody WithdrawRequest req) {
        return service.withdraw(accNo, req.amount(), req.pin());
    }

    @PostMapping("/transfer")
    public Account transfer(@Valid @RequestBody TransferRequest req) {
        return service.transfer(req);
    }

    @GetMapping("/accounts/{accNo}/transactions")
    public List<BankTransaction> history(@PathVariable String accNo) {
        return service.history(accNo);
    }

    @PostMapping("/accounts/{accNo}/close")
    public Map<String, String> close(@PathVariable String accNo, @Valid @RequestBody PinRequest req) {
        service.closeAccount(accNo, req.pin());
        return Map.of("message", "Account closed successfully");
    }

    @GetMapping("/stats")
    public Stats stats() {
        return service.stats();
    }
}
