package com.banking;

import com.banking.model.*;

// Main program to demonstrate the banking system
public class Main {
    public static void main(String[] args) {
        // Create our bank
        Bank bank = new Bank();

        // Create some customers
        Customer alice = new Customer("C001", "Alice Smith");
        Customer bob = new Customer("C002", "Bob Johnson");
        bank.addCustomer(alice);
        bank.addCustomer(bob);

        // Create accounts for Alice
        SavingsAccount aliceSavings = new SavingsAccount("SA001", 5000.00, 2.5);
        CheckingAccount aliceChecking = new CheckingAccount("CA001", 2500.00, 1000.00);
        bank.addAccount(aliceSavings, alice.getId());
        bank.addAccount(aliceChecking, alice.getId());

        // Create accounts for Bob
        SavingsAccount bobSavings = new SavingsAccount("SA002", 10000.00, 2.5);
        CheckingAccount bobChecking = new CheckingAccount("CA002", 3000.00, 1000.00);
        bank.addAccount(bobSavings, bob.getId());
        bank.addAccount(bobChecking, bob.getId());

        // Show initial state
        System.out.println("Initial Account Balances:");
        bank.printAccountSummary();

        // Do some normal transactions
        aliceSavings.deposit(1000.00);
        bobChecking.withdraw(500.00);
        aliceChecking.transfer(1000.00, bobSavings);

        // Show state after normal transactions
        System.out.println("After Normal Transactions:");
        bank.printAccountSummary();

        // Try some suspicious transactions to test fraud detection
        System.out.println("\nTesting Fraud Detection:");
        
        // Test 1: Large transaction
        System.out.println("\nTest 1: Large transaction");
        try {
            aliceSavings.withdraw(15000.00);
        } catch (IllegalStateException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        // Test 2: Rapid transactions
        System.out.println("\nTest 2: Rapid transactions");
        try {
            for (int i = 0; i < 5; i++) {
                bobChecking.deposit(100.00);
            }
        } catch (IllegalStateException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        // Show final state
        System.out.println("\nFinal Account State:");
        bank.printAccountSummary();
    }
}
