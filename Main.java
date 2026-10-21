class BankAccount {
    private final long accountNumber;
    private final String accountHolderName;
    private double balance;

    BankAccount(long accountNumber, String accountHolderName, double openingBalance) {
        if (accountHolderName == null || accountHolderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be null or empty.");
        }
        if (!Double.isFinite(openingBalance) || openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative.");
        }
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName.trim();
        this.balance = openingBalance;
    }

    public synchronized void deposit(double amount) {
        if (!isPositiveFinite(amount)) {
            System.out.printf(
                    "[%s] Deposit rejected: amount must be positive (received %.2f).%n",
                    Thread.currentThread().getName(),
                    amount);
            return;
        }

        balance += amount;
        System.out.printf(
                "[%s] Deposited %.2f. Balance: %.2f%n",
                Thread.currentThread().getName(),
                amount,
                balance);
    }

    public synchronized void withdraw(double amount) {
        if (!isPositiveFinite(amount)) {
            System.out.printf(
                    "[%s] Withdrawal rejected: amount must be positive (received %.2f).%n",
                    Thread.currentThread().getName(),
                    amount);
            return;
        }

        if (amount > balance) {
            System.out.printf(
                    "[%s] Withdrawal rejected: insufficient balance for %.2f. Balance: %.2f%n",
                    Thread.currentThread().getName(),
                    amount,
                    balance);
            return;
        }

        balance -= amount;
        System.out.printf(
                "[%s] Withdrew %.2f. Balance: %.2f%n",
                Thread.currentThread().getName(),
                amount,
                balance);
    }

    public synchronized void displayAccount() {
        System.out.printf(
                "[%s] Account number: %d | Name: %s | Balance: %.2f%n",
                Thread.currentThread().getName(),
                accountNumber,
                accountHolderName,
                balance);
    }

    private static boolean isPositiveFinite(double amount) {
        return Double.isFinite(amount) && amount > 0;
    }
}

class UserTransaction extends Thread {
    private final BankAccount bankAccount;
    private final boolean deposit;
    private final double amount;

    UserTransaction(BankAccount bankAccount, boolean deposit, double amount, String threadName) {
        super(threadName);
        this.bankAccount = bankAccount;
        this.deposit = deposit;
        this.amount = amount;
    }

    @Override
    public void run() {
        if (deposit) {
            bankAccount.deposit(amount);
        } else {
            bankAccount.withdraw(amount);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount(1001, "Ravi", 1000.00);

        System.out.println("Initial account details:");
        bankAccount.displayAccount();

        UserTransaction[] transactions = {
                new UserTransaction(bankAccount, true, 500.00, "Transaction-Deposit-1"),
                new UserTransaction(bankAccount, false, 250.00, "Transaction-Withdraw-1"),
                new UserTransaction(bankAccount, true, 300.00, "Transaction-Deposit-2"),
                new UserTransaction(bankAccount, false, 2000.00, "Transaction-Insufficient"),
                new UserTransaction(bankAccount, true, -100.00, "Transaction-Negative-Deposit"),
                new UserTransaction(bankAccount, false, 0.00, "Transaction-Zero-Withdrawal")
        };

        for (UserTransaction transaction : transactions) {
            System.out.printf(
                    "Starting %s (state=%s)%n",
                    transaction.getName(),
                    transaction.getState());
            transaction.start();
        }

        for (UserTransaction transaction : transactions) {
            try {
                transaction.join();
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread interrupted while waiting for transactions.");
                return;
            }
            System.out.printf(
                    "Joined %s (state=%s)%n",
                    transaction.getName(),
                    transaction.getState());
        }

        System.out.println("Final account details:");
        bankAccount.displayAccount();
    }
}
