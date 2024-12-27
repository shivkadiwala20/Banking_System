package com.banking.model;

import java.time.LocalDateTime;

// Represents a single bank transaction (deposit, withdrawal, transfer, etc.)
public class Transaction {
    // Basic transaction info
    private final String id;
    private final String type;
    private final double amount;
    private final String sourceAccount;
    private final String destinationAccount;
    private final LocalDateTime timestamp;
    
    // For fraud detection
    private boolean flagged;
    private String flagReason;

    // Create a new transaction. Destination can be null for deposits/withdrawals
    public Transaction(String id, String type, double amount, String sourceAccount, String destinationAccount) {
        // Check that we have all required info
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction type cannot be null or empty");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (sourceAccount == null || sourceAccount.trim().isEmpty()) {
            throw new IllegalArgumentException("Source account cannot be null or empty");
        }

        // Set up the transaction
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.timestamp = LocalDateTime.now();
        this.flagged = false;
        this.flagReason = null;
    }

    // All fields are final so we only need getters
    public String getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isFlagged() { return flagged; }
    public String getFlagReason() { return flagReason; }

    // Methods for fraud detection system to use
    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public void setFlagReason(String reason) {
        this.flagReason = reason;
    }

    /**
     * Executes the transaction on the provided bank
     * @param bank Bank where the transaction should be executed
     */
    public void execute(Bank bank) {
        Account sourceAccount = bank.getAccount(this.sourceAccount);
        if (sourceAccount == null) {
            throw new IllegalStateException("Source account not found");
        }

        if (sourceAccount.isBlocked()) {
            throw new IllegalStateException("Source account is blocked");
        }

        switch (type.toLowerCase()) {
            case "deposit":
                sourceAccount.deposit(amount);
                break;
            case "withdrawal":
                sourceAccount.withdraw(amount);
                break;
            case "transfer":
                if (destinationAccount == null) {
                    throw new IllegalStateException("Destination account required for transfer");
                }
                Account destinationAccount = bank.getAccount(this.destinationAccount);
                if (destinationAccount == null) {
                    throw new IllegalStateException("Destination account not found");
                }
                if (destinationAccount.isBlocked()) {
                    throw new IllegalStateException("Destination account is blocked");
                }
                sourceAccount.transfer(amount, destinationAccount);
                break;
            case "interest":
            case "fee":
                // These are internal transactions that don't need additional processing
                break;
            default:
                throw new IllegalStateException("Unknown transaction type: " + type);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Transaction[id=%s, type=%s, amount=%.2f, source=%s, destination=%s, timestamp=%s, flagged=%s]",
                id, type, amount, sourceAccount, destinationAccount != null ? destinationAccount : "N/A", timestamp, flagged));
        if (flagged && flagReason != null) {
            sb.append(", reason=").append(flagReason);
        }
        sb.append("]");
        return sb.toString();
    }
}
