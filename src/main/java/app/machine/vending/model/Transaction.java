package app.machine.vending.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private final LocalDateTime timestamp;
    private final String type;
    private final String description;
    private final int amount;

    public Transaction(String type, String description, int amount) {
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.description = description;
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return timestamp.format(formatter) +
                " | " + type +
                " | " + description +
                " | " + amount + " kr";
    }
}