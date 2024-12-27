package com.banking.model;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

// Main class representing the banking system.
// Manages customers, accounts, and transactions with thread-safe operations.
public class Bank {
    // Store all our customers and their accounts
    private Map<String, Customer> customers;
    private Map<String, Account> accounts;
    private List<Transaction> transactions;
    private final Object lock = new Object(); // For thread-safe operations
    private FraudDetectionSystem fraudDetectionSystem;

    // Creates a new bank instance with initialized collections
    public Bank() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.transactions = new ArrayList<>();
    }

    // Add a new customer to the bank
    public void addCustomer(Customer customer) {
        synchronized(lock) {
            if (customer == null) {
                throw new IllegalArgumentException("Customer cannot be null");
            }
            if (customers.containsKey(customer.getId())) {
                throw new IllegalArgumentException("Customer already exists with ID: " + customer.getId());
            }
            customers.put(customer.getId(), customer);
            System.out.println("Added new customer: " + customer);
        }
    }

    // Removes a customer and all their accounts
    public void removeCustomer(String customerId) {
        synchronized(lock) {
            Customer customer = customers.get(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found with ID: " + customerId);
            }
            
            // Remove all accounts belonging to the customer
            for (Account account : customer.getAccounts().values()) {
                accounts.remove(account.getAccountNumber());
            }
            
            customers.remove(customerId);
            System.out.println("Removed customer: " + customer);
        }
    }

    // Add a new account for a customer
    public void addAccount(Account account, String customerId) {
        synchronized(lock) {
            if (account == null || customerId == null) {
                throw new IllegalArgumentException("Account and customer ID cannot be null");
            }
            
            Customer customer = customers.get(customerId);
            if (customer == null) {
                throw new IllegalArgumentException("Customer not found: " + customerId);
            }

            accounts.put(account.getAccountNumber(), account);
            customer.addAccount(account);
            System.out.println("Added new account: " + account + " for customer: " + customer.getName());
        }
    }

    // Process a transaction and check for fraud
    public void processTransaction(Transaction transaction) {
        synchronized(lock) {
            if (transaction == null) {
                throw new IllegalArgumentException("Transaction cannot be null");
            }
            
            if (fraudDetectionSystem != null && fraudDetectionSystem.checkTransaction(transaction)) {
                transaction.setFlagged(true);
            }
            
            // Execute the transaction
            transaction.execute(this);
            
            // Add to transaction history
            transactions.add(transaction);
            
            // Log the transaction
            System.out.println("Processed transaction: " + transaction);
        }
    }

    // Gets transactions within a specific amount range
    public List<Transaction> getTransactionsByAmount(double minAmount, double maxAmount) {
        if (minAmount < 0 || maxAmount < minAmount) {
            throw new IllegalArgumentException("Invalid amount range");
        }
        synchronized(lock) {
            return transactions.stream()
                .filter(t -> t.getAmount() >= minAmount && t.getAmount() <= maxAmount)
                .collect(Collectors.toList());
        }
    }

    // Gets transactions for a specific account within a date range
    public List<Transaction> getAccountTransactions(String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = getAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account.getTransactionHistory(startDate, endDate);
    }

    // Gets all transactions for a specific account
    public List<Transaction> getAllAccountTransactions(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account.getAllTransactions();
    }

    // Gets transactions for a specific account
    public List<Transaction> getAccountTransactions(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }
        return account.getAllTransactions();
    }

    // Applies interest to all savings accounts
    public void applyInterestToAllAccounts() {
        synchronized(lock) {
            accounts.values().stream()
                .filter(a -> a instanceof SavingsAccount)
                .map(a -> (SavingsAccount) a)
                .forEach(SavingsAccount::applyInterest);
        }
    }

    // Gets total bank balance across all accounts
    public double getTotalBankBalance() {
        synchronized(lock) {
            return accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
        }
    }

    // Simple getters
    public Map<String, Customer> getCustomers() {
        synchronized(lock) {
            return new HashMap<>(customers);
        }
    }

    public Map<String, Account> getAccounts() {
        synchronized(lock) {
            return new HashMap<>(accounts);
        }
    }

    public List<Transaction> getTransactions() {
        synchronized(lock) {
            return new ArrayList<>(transactions);
        }
    }

    public Customer getCustomer(String customerId) {
        synchronized(lock) {
            return customers.get(customerId);
        }
    }

    public Account getAccount(String accountNumber) {
        synchronized(lock) {
            return accounts.get(accountNumber);
        }
    }

    // Get all accounts for reporting
    public List<Account> getAllAccounts() {
        synchronized(lock) {
            return new ArrayList<>(accounts.values());
        }
    }

    // Get all customers for reporting
    public List<Customer> getAllCustomers() {
        synchronized(lock) {
            return new ArrayList<>(customers.values());
        }
    }

    // Print a summary of all accounts
    public void printAccountSummary() {
        synchronized(lock) {
            System.out.println("\nBank Account Summary:");
            System.out.println("--------------------");
            for (Account account : accounts.values()) {
                System.out.printf("%s: $%.2f%n", 
                    account.getAccountNumber(), 
                    account.getBalance());
            }
            System.out.println();
        }
    }

    // Generates a summary report of all accounts
    public String generateAccountSummaryReport() {
        synchronized(lock) {
            StringBuilder report = new StringBuilder();
            report.append("=== Bank Account Summary Report ===\n");
            report.append(String.format("Total Accounts: %d\n", accounts.size()));
            report.append(String.format("Total Balance: $%.2f\n\n", getTotalBankBalance()));
            
            accounts.values().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .forEach(account -> {
                    report.append(String.format("Account: %s\n", account.getAccountNumber()));
                    report.append(String.format("Type: %s\n", account.getClass().getSimpleName()));
                    report.append(String.format("Balance: $%.2f\n", account.getBalance()));
                    if (account instanceof SavingsAccount) {
                        report.append(String.format("Interest Rate: %.2f%%\n", 
                            ((SavingsAccount) account).getInterestRate()));
                    }
                    if (account instanceof CheckingAccount) {
                        CheckingAccount ca = (CheckingAccount) account;
                        report.append(String.format("Overdraft Limit: $%.2f\n", ca.getOverdraftLimit()));
                        if (ca.isInOverdraft()) {
                            report.append(String.format("Currently Overdrawn: $%.2f\n", 
                                ca.getOverdraftAmount()));
                        }
                    }
                    report.append("Status: ").append(account.isBlocked() ? "BLOCKED" : "ACTIVE").append("\n\n");
                });
            
            return report.toString();
        }
    }

    public void setFraudDetectionSystem(FraudDetectionSystem fraudDetectionSystem) {
        this.fraudDetectionSystem = fraudDetectionSystem;
    }

    public void applyInterest() {
        synchronized(lock) {
            for (Map.Entry<String, Account> entry : accounts.entrySet()) {
                Account account = entry.getValue();
                if (account instanceof SavingsAccount) {
                    ((SavingsAccount) account).applyInterest();
                }
            }
        }
    }

    public List<Transaction> getTransactionsByAmountRange(double minAmount, double maxAmount) {
        synchronized(lock) {
            List<Transaction> result = new ArrayList<>();
            for (Account account : accounts.values()) {
                for (Transaction transaction : account.getAllTransactions()) {
                    double amount = transaction.getAmount();
                    if (amount >= minAmount && amount <= maxAmount) {
                        result.add(transaction);
                    }
                }
            }
            return result;
        }
    }

    // Get all flagged transactions
    public List<Transaction> getFlaggedTransactions() {
        synchronized(lock) {
            return transactions.stream()
                .filter(Transaction::isFlagged)
                .collect(Collectors.toList());
        }
    }

    public List<Account> getBlockedAccounts() {
        synchronized(lock) {
            List<Account> blockedAccounts = new ArrayList<>();
            for (Account account : accounts.values()) {
                if (account.isBlocked()) {
                    blockedAccounts.add(account);
                }
            }
            return blockedAccounts;
        }
    }

    @Override
    public String toString() {
        synchronized(lock) {
            return String.format("Bank[customers=%d, accounts=%d, transactions=%d, totalBalance=%.2f]",
                    customers.size(), accounts.size(), transactions.size(), getTotalBankBalance());
        }
    }
}
