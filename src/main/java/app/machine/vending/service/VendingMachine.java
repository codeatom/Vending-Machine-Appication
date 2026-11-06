package app.machine.vending.service;

import app.machine.vending.model.Product;
import app.machine.vending.model.Transaction;

import java.util.List;

public interface VendingMachine {

    boolean insertCoin(int coin);

    Product purchaseProduct(int id);

    int returnChange();

    int getBalance();

    List<Product> getProducts();

    List<Transaction> getTransactionHistory();
}
