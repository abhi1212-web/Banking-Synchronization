# Practicum 9: Banking Synchronization

This standalone Java program demonstrates synchronized operations on one
shared `BankAccount` while several user transaction threads run concurrently.

## Concepts demonstrated

- A shared `BankAccount` containing account number, account holder name, and balance
- Synchronized `deposit`, `withdraw`, and `displayAccount` methods
- `UserTransaction`, a thread class for user operations
- Concurrent mixed deposits and withdrawals
- `start()` to begin transactions and `join()` to wait for all of them
- Validation for non-positive transaction amounts
- Rejection of withdrawals greater than the available balance
- Thread names and states while transactions are started and joined

## Project structure

```text
java-banking-synchronization/
├── Main.java
└── README.md
```

## Compile and run

From this directory, run:

```bash
javac Main.java
java Main
```

The source is package-free, so no classpath or source-directory setup is
required. Compiled `.class` files can be removed with `rm -f *.class`.

## Sample behavior

Transaction order can change because the operations run concurrently. A run
includes output similar to:

```text
Initial account details:
[main] Account number: 1001 | Name: Ravi | Balance: 1000.00
Starting Transaction-Deposit-1 (state=NEW)
Starting Transaction-Withdraw-1 (state=NEW)
[Transaction-Deposit-1] Deposited 500.00. Balance: 1500.00
[Transaction-Withdraw-1] Withdrew 250.00. Balance: 1250.00
[Transaction-Insufficient] Withdrawal rejected: insufficient balance for 2000.00. Balance: 1550.00
[Transaction-Negative-Deposit] Deposit rejected: amount must be positive (received -100.00).
[Transaction-Zero-Withdrawal] Withdrawal rejected: amount must be positive (received 0.00).
Joined Transaction-Deposit-1 (state=TERMINATED)
Final account details:
[main] Account number: 1001 | Name: Ravi | Balance: 1550.00
```
