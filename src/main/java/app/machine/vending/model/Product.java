
package app.machine.vending.model;

public abstract class Product {

    private final int id;
    private final String name;
    private final int price;
    private int quantity;

    protected Product(int id, String name, int price, int quantity) {
        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive.");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public abstract String describe();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void decreaseQuantity() {
        if (quantity <= 0) {
            throw new IllegalStateException("Product is out of stock.");
        }
        quantity--;
    }
}
