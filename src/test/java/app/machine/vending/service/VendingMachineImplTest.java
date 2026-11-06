package app.machine.vending.service;

import app.machine.vending.model.*;
import app.machine.vending.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VendingMachineImplTest {
    private ProductRepository repository;
    private VendingMachineImpl vendingMachine;

    private Snack chips;
    private Beverage cola;
    private Fruit apple;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();

        chips = new Snack(1, "Chips", 20, 3, 130);
        cola = new Beverage(2, "Cola", 15, 3, 330);
        apple = new Fruit(3, "Apple", 10, 8, "Sweden");

        repository.addProduct(chips);
        repository.addProduct(cola);
        repository.addProduct(apple);

        vendingMachine = new VendingMachineImpl(repository);
    }

    // Insert valid coin.
    @Test
    void insertValidCoinShouldIncreaseBalance() {
        boolean result = vendingMachine.insertCoin(10);

        assertTrue(result);
        assertEquals(10, vendingMachine.getBalance());
    }

    // Reject invalid coin.
    @Test
    void insertInvalidCoinShouldNotChangeBalance() {
        boolean result = vendingMachine.insertCoin(7);

        assertFalse(result);
        assertEquals(0, vendingMachine.getBalance());
    }

    // Purchase succeeds.
    @Test
    void purchaseProductShouldSucceed() {
        vendingMachine.insertCoin(20);

        Product purchased = vendingMachine.purchaseProduct(1);

        assertNotNull(purchased);
        assertEquals("Chips", purchased.getName());

        assertEquals(0, vendingMachine.getBalance());
        assertEquals(2, chips.getQuantity());
    }

    // Purchase fails due to insufficient balance.
    @Test
    void purchaseShouldFailWithInsufficientBalance() {
        vendingMachine.insertCoin(10);

        assertThrows(IllegalStateException.class, () -> vendingMachine.purchaseProduct(1));

        assertEquals(10, vendingMachine.getBalance());
        assertEquals(3, chips.getQuantity());
    }

    // Purchase fails because product is out of stock.
    @Test
    void purchaseShouldFailWhenProductIsOutOfStock() {
        Snack emptySnack =
                new Snack(4, "Empty Chips", 20, 0, 130);

        repository.addProduct(emptySnack);

        vendingMachine.insertCoin(20);

        assertThrows(IllegalStateException.class, () -> vendingMachine.purchaseProduct(4));

        assertEquals(20, vendingMachine.getBalance());
        assertEquals(0, emptySnack.getQuantity());
    }

    // Return change.
    @Test
    void returnChangeShouldResetBalance() {
        vendingMachine.insertCoin(20);
        vendingMachine.insertCoin(20);
        vendingMachine.insertCoin(10);

        int change = vendingMachine.returnChange();

        assertEquals(50, change);
        assertEquals(0, vendingMachine.getBalance());
    }

    // Product list.
    @Test
    void getProductsShouldReturnAllProducts() {
        List<Product> products = vendingMachine.getProducts();

        assertEquals(3, products.size());
    }

    // Product does not exist.
    @Test
    void purchaseShouldFailWhenProductDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> vendingMachine.purchaseProduct(99));

        assertEquals(0, vendingMachine.getBalance());
    }

    // Excess balance is returned.
    @Test
    void purchaseShouldLeaveExcessBalanceForChange() {
        vendingMachine.insertCoin(20);
        vendingMachine.insertCoin(10);

        Product purchased = vendingMachine.purchaseProduct(3);

        assertNotNull(purchased);
        assertEquals(20, vendingMachine.getBalance());

        int change = vendingMachine.returnChange();

        assertEquals(20, change);
        assertEquals(0, vendingMachine.getBalance());
    }

    // Product descriptions use polymorphism.
    @Test
    void productsShouldDescribeTheirSpecificDetails() {
        assertEquals("Chips (Snack, 130g)", chips.describe());

        assertEquals("Cola (Beverage, 330ml)", cola.describe());

        assertEquals("Apple (Fruit, Sweden)", apple.describe());
    }

    // All accepted coin values.
    @Test
    void allStandardSwedishCoinsShouldBeAccepted() {
        int[] coins = {1, 2, 5, 10, 20, 50};

        for (int coin : coins) {
            assertTrue(vendingMachine.insertCoin(coin));
        }

        assertEquals(88, vendingMachine.getBalance());
    }

    // Return change when balance is zero.
    @Test
    void returnChangeWithZeroBalanceShouldReturnZero() {
        int change = vendingMachine.returnChange();

        assertEquals(0, change);
        assertEquals(0, vendingMachine.getBalance());
    }
}
