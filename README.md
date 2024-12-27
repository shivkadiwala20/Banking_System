# Advanced Banking System

A Java-based banking system that supports multiple account types, transaction processing, and fraud detection.

## Features

- Multiple account types (Savings and Checking)
- Transaction processing with history
- Fraud detection system
- Interest calculation for savings accounts
- Overdraft protection for checking accounts
- Thread-safe operations

## How to Run

1. Ensure you have Java 8 or higher installed
2. Clone this repository
3. Navigate to the project directory
4. Compile the code:
   ```bash
   javac -d bin src/main/java/com/banking/*.java src/main/java/com/banking/model/*.java
   ```
5. Run the main program:
   ```bash
   java -cp bin com.banking.Main
   ```

## Sample Output

Running the test scenario produces the following output:

```
Initial Account Balances:
--------------------
SA001: $5000.00
CA001: $2500.00
SA002: $10000.00
CA002: $3000.00

After Normal Transactions:
--------------------
SA001: $6000.00
CA001: $1500.00
SA002: $11000.00
CA002: $2500.00

Testing Fraud Detection:

Test 1: Large transaction
Blocked: Transaction flagged: Large transaction amount: $15000.00

Test 2: Rapid transactions
Blocked: Transaction flagged: Too many transactions in short time
```

## Implementation Details

### Data Structures Used
- HashMap for customer and account storage (O(1) lookup)
- ArrayList for transaction history
- Stream API for efficient filtering

### Fraud Detection
Monitors for:
1. Large transactions (>$10,000)
2. Rapid transactions (>3 per minute)
3. Suspicious patterns:
   - Multiple small deposits followed by large withdrawal
   - Repeated identical amounts
   - Late night transactions

### Thread Safety
All bank operations are synchronized to prevent race conditions

## Known Issues
- None at time of submission

## Future Enhancements
1. Database integration for persistent storage
2. API endpoints for external integration
3. More sophisticated fraud detection algorithms
4. Support for additional account types
5. Mobile banking features

## Design Decisions

1. **Abstract Account Class**: Base class for all account types, enforcing common behavior while allowing specific implementations

2. **Fraud Detection**: Real-time monitoring system that can flag suspicious transactions and block accounts

3. **Transaction History**: Efficient storage and retrieval using Java collections and streams

4. **Error Handling**: Comprehensive validation and clear error messages

5. **Thread Safety**: Synchronized operations to support concurrent access
