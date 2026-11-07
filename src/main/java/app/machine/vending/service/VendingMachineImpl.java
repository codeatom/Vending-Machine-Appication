package app.machine.vending.service;

import app.machine.vending.model.Product;
import app.machine.vending.model.Transaction;
import app.machine.vending.repository.ProductRepository;
import app.machine.vending.repository.TransactionRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VendingMachineImpl implements VendingMachine {

    private int balance;
    private final ProductRepository repository;
    private final TransactionRepository transactionRepository;
    private final AdminService adminService;

    private final Set<Integer> acceptedCoins = Set.of(1, 2, 5, 10, 20, 50);

    public VendingMachineImpl(
            ProductRepository repository,
            TransactionRepository transactionRepository,
            AdminService adminService) {

        this.repository = Objects.requireNonNull(repository);
        this.transactionRepository = Objects.requireNonNull(transactionRepository);
        this.adminService = Objects.requireNonNull(adminService);

        this.balance = 0;
    }

    @Override
    public boolean insertCoin(int coin) {
        if (!acceptedCoins.contains(coin)) {
            return false;
        }

        balance = Math.addExact(balance, coin);

        // Record coin insertion in transaction history.
        transactionRepository.addTransaction(
                new Transaction(
                        "COIN INSERTED",
                        "Inserted coin",
                        coin
                )
        );

        return true;
    }

    @Override
    public Product purchaseProduct(int id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product with ID " + id + " does not exist."));

        if (product.getQuantity() == 0) {
            throw new IllegalStateException(product.getName() + " is out of stock.");
        }

        if (balance < product.getPrice()) {
            int missingAmount = product.getPrice() - balance;

            throw new IllegalStateException(
                    "Insufficient balance. " +
                            product.getName() + " costs " +
                            product.getPrice() + " kr. " +
                            "You need " + missingAmount + " kr more."
            );
        }

        // Deduct the product price.
        balance -= product.getPrice();

        // Reduce product stock.
        product.decreaseQuantity();

        // Record successful purchase.
        transactionRepository.addTransaction(
                new Transaction(
                        "PURCHASE",
                        product.getName() + " (ID: " + id + ")",
                        product.getPrice()
                )
        );

        return product;
    }

    @Override
    public int returnChange() {
        int change = balance;
        balance = 0;

        if (change > 0) {
            transactionRepository.addTransaction(
                    new Transaction(
                            "CHANGE RETURNED",
                            "Returned remaining balance",
                            change
                    )
            );
        }

        return change;
    }

    @Override
    public void stockProduct(int id, int quantity) {

        if (!adminService.isAuthenticated()) {
            throw new SecurityException("Access denied. Administrator login required.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Stocking quantity must be positive."
            );
        }

        Product product = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product with ID " + id + " does not exist."
                ));

        product.increaseQuantity(quantity);

        transactionRepository.addTransaction(
                new Transaction(
                        "STOCKING",
                        product.getName() + " (ID: " + id + "), added: " + quantity,
                        quantity
                )
        );
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public List<Product> getProducts() {
        return repository.findAll();
    }

    @Override
    public List<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }
}
