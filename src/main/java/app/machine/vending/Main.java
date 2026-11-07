package app.machine.vending;

import app.machine.vending.model.*;
import app.machine.vending.repository.*;
import app.machine.vending.service.*;

public class Main {

    public static void main(String[] args) {
        // Create products.
        Product chips = new Snack(1, "Chips", 15, 5, 130);
        Product cola = new Beverage(2, "Cola", 20, 3, 330);
        Product apple = new Fruit(3, "Apple", 10, 8, "Sweden");

        // Create repositories.
        ProductRepository productRepository = new InMemoryProductRepository();
        TransactionRepository transactionRepository = new InMemoryTransactionRepository();

        productRepository.addProduct(chips);
        productRepository.addProduct(cola);
        productRepository.addProduct(apple);

        // Create services.
        AdminService adminService = new AdminServiceImpl();

        // Create vending machine.
        VendingMachine vendingMachine = new VendingMachineImpl(productRepository, transactionRepository, adminService);

        // Start console UI.
        ConsoleUI consoleUI = new ConsoleUI(vendingMachine, adminService);

        consoleUI.start();
    }
}
