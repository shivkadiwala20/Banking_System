package com.banking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

// Base class for all bank accounts. Can't create this directly - must create either Savings or Checking account
public abstract class Account {
    // Store the basic account info
    protected String accountNumber;
    protected double balance;
    protected List<Transaction> transactionHistory;
    protected LocalDateTime creationDate;
    protected boolean isBlocked;
    private static long transactionCounter = 0;

    // Set up a new account with a number and starting balance
    public Account(String accountNumber, double initialBalance) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        this.creationDate = LocalDateTime.now();
        this.isBlocked = false;
    }

    // Simple getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public boolean isBlocked() { return isBlocked; }
    public LocalDateTime getCreationDate() { return creationDate; }

    // Block or unblock the account
    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
        if (blocked) {
            System.out.println("Account " + accountNumber + " has been blocked due to suspicious activity");
        }
    }

    // Get all transactions, but don't let them be modified
    public List<Transaction> getAllTransactions() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // Add money to the account
    public void deposit(double amount) {
        if (isBlocked) {
            throw new IllegalStateException("Account is blocked");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        balance += amount;
        Transaction transaction = new Transaction(
            generateTransactionId(),
            "deposit",
            amount,
            accountNumber,
            null
        );
        transactionHistory.add(transaction);
    }

    // Take money out if there's enough
    public void withdraw(double amount) {
        if (isBlocked) {
            throw new IllegalStateException("Account is blocked");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds: available balance is " + balance);
        }

        balance -= amount;
        Transaction transaction = new Transaction(
            generateTransactionId(),
            "withdrawal",
            amount,
            accountNumber,
            null
        );
        transactionHistory.add(transaction);
    }

    // Send money to another account
    public void transfer(double amount, Account destinationAccount) {
        if (isBlocked || destinationAccount.isBlocked) {
            throw new IllegalStateException("One of the accounts is blocked");
        }
        if (destinationAccount == null) {
            throw new IllegalArgumentException("Destination account cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds: available balance is " + balance);
        }

        balance -= amount;
        destinationAccount.balance += amount;
        
        Transaction transaction = new Transaction(
            generateTransactionId(),
            "transfer",
            amount,
            accountNumber,
            destinationAccount.getAccountNumber()
        );
        transactionHistory.add(transaction);
        destinationAccount.transactionHistory.add(transaction);
    }

    // Get transactions between two dates
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionHistory.stream()
            .filter(t -> !t.getTimestamp().isBefore(startDate) && !t.getTimestamp().isAfter(endDate))
            .collect(Collectors.toList());
    }

    // Get transactions by amount range
    public List<Transaction> getTransactionsByAmountRange(double minAmount, double maxAmount) {
        return transactionHistory.stream()
            .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
            .collect(Collectors.toList());
    }

    // Get transaction history within a date range
    public List<Transaction> getTransactionHistory(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        
        return transactionHistory.stream()
            .filter(t -> !t.getTimestamp().isBefore(startDate) && !t.getTimestamp().isAfter(endDate))
            .collect(Collectors.toList());
    }

    // Create a unique ID for each transaction
    protected String generateTransactionId() {
        return "T" + String.format("%03d", ++transactionCounter);
    }

    @Override
    public String toString() {
        return String.format("Account[number=%s, balance=%.2f, blocked=%s, created=%s]",
                accountNumber, balance, isBlocked, creationDate);
    }
}
