package app.machine.vending.repository;

import app.machine.vending.model.Transaction;
import java.util.List;

public interface TransactionRepository {

    void addTransaction(Transaction transaction);

    List<Transaction> findAll();
}