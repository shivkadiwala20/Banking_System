package com.banking.model;

// A checking account that allows overdrafts up to a limit
public class CheckingAccount extends Account {
    private double overdraftLimit;
    private static final double OVERDRAFT_FEE = 35.0;  // Fee charged when going into overdraft

    // Create a new checking account with an overdraft limit
    public CheckingAccount(String accountNumber, double initialBalance, double overdraftLimit) {
        super(accountNumber, initialBalance);
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overdraftLimit = overdraftLimit;
    }

    // Simple getters and setters
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        if (overdraftLimit < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
        this.overdraftLimit = overdraftLimit;
    }

    // Check if account is currently overdrawn
    public boolean isInOverdraft() {
        return balance < 0;
    }

    // Allow withdrawals up to the overdraft limit, but charge a fee if going negative
    @Override
    public void withdraw(double amount) {
        if (isBlocked) {
            throw new IllegalStateException("Account is blocked");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        // Check if withdrawal would exceed available balance plus overdraft
        double availableBalance = balance + overdraftLimit;
        if (amount > availableBalance) {
            throw new IllegalStateException("Amount exceeds available balance with overdraft: " + availableBalance);
        }

        // Take out the money
        balance -= amount;
        Transaction withdrawalTransaction = new Transaction(
            generateTransactionId(),
            "withdrawal",
            amount,
            accountNumber,
            null
        );
        transactionHistory.add(withdrawalTransaction);

        // If we went into overdraft, charge the fee
        if (balance < 0) {
            balance -= OVERDRAFT_FEE;
            Transaction feeTransaction = new Transaction(
                generateTransactionId(),
                "fee",
                OVERDRAFT_FEE,
                accountNumber,
                null
            );
            transactionHistory.add(feeTransaction);
        }
    }

    // Gets the current overdraft amount if any
    public double getOverdraftAmount() {
        return balance < 0 ? Math.abs(balance) : 0;
    }

    @Override
    public String toString() {
        return String.format("CheckingAccount[number=%s, balance=%.2f, overdraftLimit=%.2f, inOverdraft=%s, blocked=%s]",
                accountNumber, balance, overdraftLimit, isInOverdraft(), isBlocked);
    }
}
