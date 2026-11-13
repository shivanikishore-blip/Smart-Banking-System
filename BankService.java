package com.smartbank.service;

import com.smartbank.dto.Dtos.*;
import com.smartbank.exception.BankException;
import com.smartbank.model.Account;
import com.smartbank.model.BankTransaction;
import com.smartbank.repository.AccountRepository;
import com.smartbank.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;

@Service
public class BankService {

    private final AccountRepository accounts;
    private final TransactionRepository txns;
    private final SecureRandom random = new SecureRandom();

    public BankService(AccountRepository accounts, TransactionRepository txns) {
        this.accounts = accounts;
        this.txns = txns;
    }

    // ---------- helpers ----------
    private String hash(String pin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(pin.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Account find(String accNo) {
        return accounts.findByAccountNumber(accNo)
                .orElseThrow(() -> new BankException("Account " + accNo + " not found", HttpStatus.NOT_FOUND));
    }

    private void verifyPin(Account acc, String pin) {
        if (!acc.getPinHash().equals(hash(pin))) {
            throw new BankException("Incorrect PIN", HttpStatus.UNAUTHORIZED);
        }
    }

    private String generateAccountNumber() {
        String no;
        do {
            no = "SB" + (1000000000L + (long) (random.nextDouble() * 8999999999L));
        } while (accounts.existsByAccountNumber(no));
        return no;
    }

    // ---------- use cases ----------
    @Transactional
    public Account createAccount(CreateAccountRequest r) {
        String type = r.accountType().toUpperCase();
        if (!type.equals("SAVINGS") && !type.equals("CURRENT")) {
            throw new BankException("Account type must be SAVINGS or CURRENT");
        }
        Account a = new Account();
        a.setAccountNumber(generateAccountNumber());
        a.setHolderName(r.holderName().trim());
        a.setEmail(r.email().trim());
        a.setPhone(r.phone());
        a.setAccountType(type);
        a.setPinHash(hash(r.pin()));
        a.setBalance(r.initialDeposit());
        a = accounts.save(a);
        txns.save(new BankTransaction(a.getAccountNumber(), "DEPOSIT", r.initialDeposit(),
                a.getBalance(), "Account opening deposit"));
        return a;
    }

    public Account login(LoginRequest r) {
        Account a = find(r.accountNumber().trim());
        verifyPin(a, r.pin());
        return a;
    }

    public Account getAccount(String accNo) {
        return find(accNo);
    }

    public List<Account> allAccounts() {
        return accounts.findAll();
    }

    @Transactional
    public Account deposit(String accNo, BigDecimal amount) {
        Account a = find(accNo);
        a.setBalance(a.getBalance().add(amount));
        accounts.save(a);
        txns.save(new BankTransaction(accNo, "DEPOSIT", amount, a.getBalance(), "Cash deposit"));
        return a;
    }

    @Transactional
    public Account withdraw(String accNo, BigDecimal amount, String pin) {
        Account a = find(accNo);
        verifyPin(a, pin);
        if (a.getBalance().compareTo(amount) < 0) {
            throw new BankException("Insufficient balance");
        }
        a.setBalance(a.getBalance().subtract(amount));
        accounts.save(a);
        txns.save(new BankTransaction(accNo, "WITHDRAW", amount, a.getBalance(), "Cash withdrawal"));
        return a;
    }

    @Transactional
    public Account transfer(TransferRequest r) {
        if (r.fromAccount().equals(r.toAccount())) {
            throw new BankException("Cannot transfer to the same account");
        }
        Account from = find(r.fromAccount());
        Account to = find(r.toAccount());
        verifyPin(from, r.pin());
        if (from.getBalance().compareTo(r.amount()) < 0) {
            throw new BankException("Insufficient balance");
        }
        from.setBalance(from.getBalance().subtract(r.amount()));
        to.setBalance(to.getBalance().add(r.amount()));
        accounts.save(from);
        accounts.save(to);

        String note = (r.remark() == null || r.remark().isBlank()) ? "" : " - " + r.remark().trim();
        txns.save(new BankTransaction(from.getAccountNumber(), "TRANSFER_OUT", r.amount(),
                from.getBalance(), "Sent to " + to.getAccountNumber() + note));
        txns.save(new BankTransaction(to.getAccountNumber(), "TRANSFER_IN", r.amount(),
                to.getBalance(), "Received from " + from.getAccountNumber() + note));
        return from;
    }

    public List<BankTransaction> history(String accNo) {
        find(accNo);
        return txns.findByAccountNumberOrderByCreatedAtDesc(accNo);
    }

    @Transactional
    public void closeAccount(String accNo, String pin) {
        Account a = find(accNo);
        verifyPin(a, pin);
        txns.deleteByAccountNumber(accNo);
        accounts.delete(a);
    }

    public Stats stats() {
        return new Stats(accounts.count(), accounts.totalBalance(), txns.count());
    }
}
