package com.banking.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Monitors transactions for suspicious activity
public class FraudDetectionSystem {
    // Thresholds for detecting suspicious activity
    private static final double LARGE_TRANSACTION_THRESHOLD = 10000.00;
    private static final int MAX_TRANSACTIONS_PER_MINUTE = 3;
    
    // Keep track of all transactions by account
    private Map<String, List<Transaction>> accountTransactions;

    public FraudDetectionSystem() {
        accountTransactions = new HashMap<>();
    }

    // Check a new transaction for suspicious activity
    public boolean checkTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        boolean isSuspicious = false;

        // Check for large amounts
        if (transaction.getAmount() >= LARGE_TRANSACTION_THRESHOLD) {
            flagTransaction(transaction, "Large transaction amount: $" + transaction.getAmount());
            isSuspicious = true;
        }

        // Get this account's transaction history
        String accountNumber = transaction.getSourceAccount();
        List<Transaction> transactions = accountTransactions.computeIfAbsent(
            accountNumber, k -> new ArrayList<>()
        );
        transactions.add(transaction);

        // Check for too many transactions too quickly
        if (isRapidTransactions(transactions)) {
            flagTransaction(transaction, "Too many transactions in a short time");
            isSuspicious = true;
        }

        // Check for unusual patterns
        if (hasUnusualPattern(transactions)) {
            flagTransaction(transaction, "Unusual transaction pattern detected");
            isSuspicious = true;
        }

        return isSuspicious;
    }

    // Flag a transaction as suspicious
    private void flagTransaction(Transaction transaction, String reason) {
        transaction.setFlagged(true);
        transaction.setFlagReason(reason);
        System.out.println("Warning: " + reason + " for account " + transaction.getSourceAccount());
    }

    // Check if there are too many transactions in the last minute
    private boolean isRapidTransactions(List<Transaction> transactions) {
        if (transactions.size() < MAX_TRANSACTIONS_PER_MINUTE) {
            return false;
        }

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        int recentCount = 0;

        // Count transactions in the last minute
        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);
            if (t.getTimestamp().isAfter(oneMinuteAgo)) {
                recentCount++;
                if (recentCount > MAX_TRANSACTIONS_PER_MINUTE) {
                    return true;
                }
            } else {
                break;
            }
        }
        return false;
    }

    // Additional fraud detection patterns
    private boolean hasUnusualPattern(List<Transaction> transactions) {
        if (transactions.size() < 3) return false;

        // Pattern 1: Multiple small deposits followed by large withdrawal
        int smallDeposits = 0;
        double totalDeposits = 0;
        for (Transaction t : transactions) {
            if (t.getType().equals("deposit") && t.getAmount() < 1000) {
                smallDeposits++;
                totalDeposits += t.getAmount();
            }
            if (t.getType().equals("withdrawal") && 
                smallDeposits >= 2 && 
                t.getAmount() >= totalDeposits * 0.9) {
                return true;
            }
        }

        // Pattern 2: Repeated identical amounts
        Map<Double, Integer> amountFrequency = new HashMap<>();
        for (Transaction t : transactions) {
            amountFrequency.merge(t.getAmount(), 1, Integer::sum);
            if (amountFrequency.get(t.getAmount()) >= 3) {
                return true;
            }
        }

        // Pattern 3: Unusual hour transactions
        LocalDateTime now = LocalDateTime.now();
        long lateNightTransactions = transactions.stream()
            .filter(t -> {
                int hour = t.getTimestamp().getHour();
                return hour >= 23 || hour <= 4;
            })
            .count();
        if (lateNightTransactions >= 2) {
            return true;
        }

        return false;
    }
}
