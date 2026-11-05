
package app.machine.vending.service;

import app.machine.vending.model.Product;
import app.machine.vending.repository.ProductRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class VendingMachineImpl implements VendingMachine {

    private int balance;

    private final ProductRepository repository;

    private final Set<Integer> acceptedCoins =
            Set.of(1, 2, 5, 10, 20, 50);

    public VendingMachineImpl(ProductRepository repository) {
        this.repository = Objects.requireNonNull(
                repository, "Repository cannot be null."
        );
        this.balance = 0;
    }

    @Override
    public boolean insertCoin(int coin) {

        if (!acceptedCoins.contains(coin)) {
            return false;
        }

        balance = Math.addExact(balance, coin);
        return true;
    }

    @Override
    public Product purchaseProduct(int id) {

        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product with ID " + id + " does not exist."
                        )
                );

        if (product.getQuantity() == 0) {
            throw new IllegalStateException(
                    product.getName() + " is out of stock."
            );
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

        return product;
    }

    @Override
    public int returnChange() {

        int change = balance;
        balance = 0;

        return change;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public List<Product> getProducts() {
        return repository.findAll();
    }
}
