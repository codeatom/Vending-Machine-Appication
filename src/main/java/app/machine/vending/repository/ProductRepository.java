
package app.machine.vending.repository;

import app.machine.vending.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    void addProduct(Product product);

    Optional<Product> findById(int id);

    List<Product> findAll();
}
