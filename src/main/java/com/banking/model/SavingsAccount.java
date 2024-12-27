package com.banking.model;

import java.time.LocalDateTime;

// A savings account that earns interest on its balance
public class SavingsAccount extends Account {
    private double interestRate;  // Annual interest rate (like 2.5 for 2.5%)
    private LocalDateTime lastInterestApplied;

    // Create a new savings account with an interest rate
    public SavingsAccount(String accountNumber, double initialBalance, double interestRate) {
        super(accountNumber, initialBalance);
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        this.interestRate = interestRate;
        this.lastInterestApplied = LocalDateTime.now();
    }

    // Simple getters and setters
    public double getInterestRate() { return interestRate; }

    public void setInterestRate(double interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        this.interestRate = interestRate;
    }

    // Add interest to the account based on current balance
    public void applyInterest() {
        if (!isBlocked) {
            double interestAmount = balance * (interestRate / 100.0);
            balance += interestAmount;
            
            Transaction interestTransaction = new Transaction(
                generateTransactionId(),
                "interest",
                interestAmount,
                accountNumber,
                null
            );
            transactionHistory.add(interestTransaction);
            
            lastInterestApplied = LocalDateTime.now();
            System.out.printf("Applied %.2f%% interest to account %s. Interest amount: $%.2f%n",
                    interestRate, accountNumber, interestAmount);
        } else {
            System.out.println("Cannot apply interest to blocked account " + accountNumber);
        }
    }

    /**
     * Gets the date when interest was last applied
     * @return DateTime of last interest application
     */
    public LocalDateTime getLastInterestApplied() {
        return lastInterestApplied;
    }

    @Override
    public String toString() {
        return String.format("SavingsAccount[number=%s, balance=%.2f, interestRate=%.2f%%, blocked=%s]",
                accountNumber, balance, interestRate, isBlocked);
    }
}
