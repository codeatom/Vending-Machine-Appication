
package app.machine.vending.model;

public class Snack extends Product {

    private final int weightGrams;

    public Snack(int id, String name, int price,
                 int quantity, int weightGrams) {
        super(id, name, price, quantity);

        if (weightGrams <= 0) {
            throw new IllegalArgumentException(
                    "Weight must be positive."
            );
        }

        this.weightGrams = weightGrams;
    }

    @Override
    public String describe() {
        return getName() + " (Snack, " + weightGrams + "g)";
    }
}
