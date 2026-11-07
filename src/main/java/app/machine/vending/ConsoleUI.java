package app.machine.vending;

import app.machine.vending.model.Product;
import app.machine.vending.service.VendingMachine;

import java.util.Scanner;

public class ConsoleUI {
    private final VendingMachine vendingMachine;
    private final Scanner scanner;

    public ConsoleUI(VendingMachine vendingMachine) {
        this.vendingMachine = vendingMachine;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Lexicon Vending Machine");

        displayProducts();

        boolean running = true;

        while (running) {
            System.out.println("\n------------------------------------");
            System.out.println("Balance: " + vendingMachine.getBalance() + " kr");

            System.out.println("\nChoose an action:");
            System.out.println("[1] Insert coin");
            System.out.println("[2] Select product");
            System.out.println("[3] Return change");
            System.out.println("[4] Display products");
            System.out.println("[5] Display transaction history");
            System.out.println("[6] Stock product");
            System.out.println("[7] Exit");

            System.out.print("\n> ");

            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> insertCoin();
                    case "2" -> selectProduct();
                    case "3" -> returnChange();
                    case "4" -> displayProducts();
                    case "5" -> displayTransactionHistory();
                    case "6" -> stockProduct();
                    case "7" -> running = false;
                    default -> System.out.println(
                            "Invalid option. Please try again."
                    );
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }

            System.out.println("Balance: " + vendingMachine.getBalance() + " kr");
        }

        System.out.println("\nThank you for using Lexicon Vending Machine!");
        scanner.close();
    }

    private void displayProducts() {
        System.out.println("\n------------------------------------");

        for (Product product : vendingMachine.getProducts()) {

            // Polymorphism: describe() depends on the product type.
            System.out.printf(
                    "[%d] %-12s - %d kr  (%s)  Stock: %d%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.describe().substring(
                            product.getName().length() + 1,
                            product.describe().length() - 1
                    ),
                    product.getQuantity()
            );
        }

        System.out.println("------------------------------------");
    }

    private void insertCoin() {
        System.out.print("Insert coin: ");

        int coin = Integer.parseInt(scanner.nextLine().trim());

        boolean accepted = vendingMachine.insertCoin(coin);

        if (accepted) {
            System.out.println("Coin accepted.");
        } else {
            System.out.println("Invalid coin. Only 1, 2, 5, 10, 20, 50 kr accepted.");
        }
    }

    private void selectProduct() {
        System.out.print("Select product ID: ");

        int id = Integer.parseInt(scanner.nextLine().trim());

        try {
            Product product = vendingMachine.purchaseProduct(id);

            System.out.println("Dispensing: " + product.describe());

            // Automatically return any remaining balance as change.
            int change = vendingMachine.returnChange();

            if (change > 0) {
                System.out.println("Change returned: " + change + " kr");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void returnChange() {
        int change = vendingMachine.returnChange();

        if (change == 0) {
            System.out.println("No balance to return.");
        } else {
            System.out.println("Change returned: " + change + " kr");
        }
    }

    private void displayTransactionHistory() {

        System.out.println("\n========== TRANSACTION HISTORY ==========");

        var transactions = vendingMachine.getTransactionHistory();

        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
        } else {

            for (var transaction : transactions) {
                System.out.println(transaction);
            }
        }

        System.out.println("=========================================");
    }

    private void stockProduct() {
        System.out.print("Enter product ID: ");

        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter quantity to add: ");

        int quantity = Integer.parseInt(scanner.nextLine().trim());

        try {
            vendingMachine.stockProduct(id, quantity);

            System.out.println("Product successfully restocked.");

            displayProducts();

        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Stocking failed: " + e.getMessage());
        }
    }
}
