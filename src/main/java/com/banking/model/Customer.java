package com.banking.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

/**
 * Represents a bank customer and manages their accounts.
 * Uses HashMap for efficient account lookup and management.
 */
public class Customer {
    private String id;
    private String name;
    private Map<String, Account> accounts;

    /**
     * Creates a new customer
     * @param id Unique customer identifier
     * @param name Customer's name
     * @throws IllegalArgumentException if id or name is invalid
     */
    public Customer(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        
        this.id = id;
        this.name = name;
        this.accounts = new HashMap<>();
    }

    /**
     * Gets the customer's ID
     * @return Customer ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the customer's name
     * @return Customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds a new account for this customer
     * @param account Account to be added
     * @throws IllegalArgumentException if account is null or already exists
     */
    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }
        if (accounts.containsKey(account.getAccountNumber())) {
            throw new IllegalArgumentException("Account already exists for this customer");
        }
        accounts.put(account.getAccountNumber(), account);
    }

    /**
     * Removes an account from this customer
     * @param accountNumber Account number to remove
     * @throws IllegalArgumentException if account not found
     */
    public void removeAccount(String accountNumber) {
        if (!accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account not found for this customer");
        }
        accounts.remove(accountNumber);
    }

    /**
     * Gets an account by its number
     * @param accountNumber Account number to look up
     * @return Account object
     * @throws IllegalArgumentException if account not found
     */
    public Account getAccount(String accountNumber) {
        if (!accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Account not found for this customer");
        }
        return accounts.get(accountNumber);
    }

    /**
     * Gets an unmodifiable view of customer's accounts
     * @return Map of account numbers to accounts
     */
    public Map<String, Account> getAccounts() {
        return Collections.unmodifiableMap(accounts);
    }

    /**
     * Gets total balance across all accounts
     * @return Total balance
     */
    public double getTotalBalance() {
        return accounts.values().stream()
                .mapToDouble(Account::getBalance)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("Customer[id=%s, name=%s, accounts=%d, totalBalance=%.2f]",
                id, name, accounts.size(), getTotalBalance());
    }
}
