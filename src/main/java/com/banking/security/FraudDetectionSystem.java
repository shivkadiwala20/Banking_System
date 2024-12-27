package com.banking.security;

import com.banking.model.*;
import java.time.LocalDateTime;
import java.util.*;

public class FraudDetectionSystem {
    private List<Transaction> suspiciousTransactions;
    private Set<String> blacklistedAccounts;
    private static final double SUSPICIOUS_AMOUNT_THRESHOLD = 10000.00;
    private static final int MAX_TRANSACTIONS_PER_MINUTE = 3;
    private Map<String, List<LocalDateTime>> transactionTimestamps;

    public FraudDetectionSystem() {
        this.suspiciousTransactions = new ArrayList<>();
        this.blacklistedAccounts = new HashSet<>();
        this.transactionTimestamps = new HashMap<>();
    }

    public void monitorTransactions(Bank bank) {
        Collection<Transaction> transactions = bank.getAllTransactions();
        for (Transaction transaction : transactions) {
            if (!transaction.isFlagged()) {
                checkTransaction(transaction);
            }
        }
    }

    private void checkTransaction(Transaction transaction) {
        // Check for large amounts
        if (transaction.getAmount() > SUSPICIOUS_AMOUNT_THRESHOLD) {
            flagTransaction(transaction);
        }

        // Check for rapid transactions
        String accountNumber = transaction.getSourceAccountNumber();
        List<LocalDateTime> timestamps = transactionTimestamps.getOrDefault(accountNumber, new ArrayList<>());
        timestamps.add(transaction.getTimestamp());
        
        // Remove timestamps older than 1 minute
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        timestamps.removeIf(time -> time.isBefore(oneMinuteAgo));

        if (timestamps.size() > MAX_TRANSACTIONS_PER_MINUTE) {
            flagTransaction(transaction);
            blockAccount(accountNumber);
        }

        transactionTimestamps.put(accountNumber, timestamps);
    }

    public void flagTransaction(Transaction transaction) {
        transaction.setFlagged(true);
        suspiciousTransactions.add(transaction);
    }

    public void blockAccount(String accountNumber) {
        blacklistedAccounts.add(accountNumber);
    }

    public List<Transaction> getSuspiciousTransactions() {
        return new ArrayList<>(suspiciousTransactions);
    }

    public Set<String> getBlacklistedAccounts() {
        return new HashSet<>(blacklistedAccounts);
    }
}
