package app.machine.vending;

import app.machine.vending.model.Product;
import app.machine.vending.service.AdminService;
import app.machine.vending.service.VendingMachine;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner;
    private final VendingMachine vendingMachine;
    private final AdminService adminService;

    private boolean running = true;

    public ConsoleUI(VendingMachine vendingMachine, AdminService adminService) {
        this.scanner = new Scanner(System.in);
        this.vendingMachine = vendingMachine;
        this.adminService = adminService;
    }

    public void start() {
        System.out.println("\nWelcome to Lexicon Vending Machine");
        displayProducts();

        while (running) {
            System.out.println("\nBalance: " + vendingMachine.getBalance() + " kr");

            System.out.println("\nChoose an action:");
            System.out.println("[1] Insert coin,     [2] Select product,");
            System.out.println("[3] Return change,   [4] Display products,");
            System.out.println("[5] Login admin,");

            System.out.print("\n> ");

            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1" -> insertCoin();
                    case "2" -> selectProduct();
                    case "3" -> returnChange();
                    case "4" -> displayProducts();
                    case "5" -> adminLogin();
                    default -> System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            
        }

        System.out.println("\nThank you for using Lexicon Vending Machine!");
        scanner.close();
    }

    private void displayProducts() {
        System.out.println("\n==========Available to purchase==========");

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
        displayProducts();
        System.out.print("Select a product ID: ");

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

    private void reStockProduct() {
        if (!adminService.isAuthenticated()) {
            System.out.println("Access denied. Please log in as administrator.");
            return;
        }

        System.out.print("Enter product ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter quantity to add: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        try {
            vendingMachine.stockProduct(id, quantity);

            System.out.println("Product successfully restocked.");

            displayProducts();

        } catch (IllegalArgumentException |
                 IllegalStateException |
                 ArithmeticException e) {

            System.out.println("Stocking failed: " + e.getMessage());
        }
    }

    private void adminLogin() {
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        if (adminService.login(username, password)) {
            handleAdmin(vendingMachine, scanner);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void adminLogout() {
        if (!adminService.isAuthenticated()) {
            System.out.println("No administrator is logged in.");
            return;
        }

        adminService.logout();
        System.out.println("Administrator logged out.");
    }

    private void handleAdmin(VendingMachine vendingMachine, Scanner scanner) {
        if (!adminService.isAuthenticated()) {
            System.out.println("Access denied!");
            return;
        }

        boolean adminRunning = true;

        while (adminRunning) {
            System.out.println("\n===== ADMIN PANEL =====");
            System.out.println("1. Restock product");
            System.out.println("2. View products");
            System.out.println("3. View low-stock products");
            System.out.println("4. Display transaction history");
            System.out.println("5. Admin exit");
            System.out.println("6. Exit application");
            System.out.print("\nChoose option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    displayAdminProducts(vendingMachine);
                    reStockProduct();
                }

                case "2" -> displayAdminProducts(vendingMachine);

                case "3" -> displayLowStockProducts(vendingMachine);

                case "4" -> displayTransactionHistory();

                case "5" -> {
                    System.out.println("Admin logged out.");
                    adminLogout();
                    adminRunning = false;
                }

                case "6" -> {
                    adminLogout();
                    running = false;
                    adminRunning = false;
                }

                default -> System.out.println("Invalid admin option.");
            }
        }
    }

    private static void displayAdminProducts(VendingMachine vendingMachine) {
        System.out.println("\n-----------Products in stock-----------");

        List<Product> products = vendingMachine.getProducts();

        for (Product p : products) {
            System.out.printf("[%d] %-12s - %2d kr  (%-20s) Stock: %d%n",
                    p.getId(), p.getName(), p.getPrice(),
                    p.describe(), p.getQuantity());
        }

        System.out.println("------------------------------------");
    }

    private static void displayLowStockProducts(VendingMachine vendingMachine) {
        List<Product> lowStockProducts = vendingMachine.getLowStockProducts();

        System.out.println("\n===== LOW STOCK ALERT =====");

        if (lowStockProducts.isEmpty()) {
            System.out.println("All products are sufficiently stocked!");
            return;
        }

        System.out.println("WARNING: The following products have low stock!");

        for (Product product : lowStockProducts) {
            System.out.printf("[%d] %-12s - Stock: %d%n",
                    product.getId(),
                    product.getName(),
                    product.getQuantity());
        }

        System.out.println("===========================\n");
    }

}
