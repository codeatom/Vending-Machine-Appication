package app.machine.vending.model;

public class Fruit extends Product {

    private final String origin;

    public Fruit(int id, String name, int price, int quantity, String origin) {
        super(id, name, price, quantity);

        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin cannot be empty.");
        }

        this.origin = origin;
    }

    @Override
    public String describe() {
        return getName() + " (Fruit, " + origin + ")";
    }
}
