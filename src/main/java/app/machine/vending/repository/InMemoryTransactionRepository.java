package app.machine.vending.repository;

import app.machine.vending.model.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InMemoryTransactionRepository implements TransactionRepository {

    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void addTransaction(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null.");

        transactions.add(transaction);
    }

    @Override
    public List<Transaction> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }
}