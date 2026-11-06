package app.machine.vending.repository;

import app.machine.vending.model.Product;

import java.util.*;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<Integer, Product> products = new LinkedHashMap<>();

    @Override
    public void addProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null.");

        if (products.containsKey(product.getId())) {
            throw new IllegalArgumentException("Product ID already exists: " + product.getId());
        }

        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }
}
