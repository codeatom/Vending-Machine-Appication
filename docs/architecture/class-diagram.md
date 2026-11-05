# Vending Machine — Class Diagram

```mermaid
classDiagram
direction TB

namespace model {
    class Product {
        <<abstract>>
        -int id
        -String name
        -int price
        -int quantity
        +describe() String
        +getId() int
        +getName() String
        +getPrice() int
        +getQuantity() int
        +decreaseQuantity() void
    }

    class Snack {
        -int weightGrams
        +describe() String
    }

    class Beverage {
        -int volumeMl
        +describe() String
    }

    class Fruit {
        -String origin
        +describe() String
    }
}

namespace repository {
    class ProductRepository {
        <<interface>>
        +addProduct(Product) void
        +findById(int) Optional~Product~
        +findAll() List~Product~
    }

    class InMemoryProductRepository {
        -Map~Integer, Product~ products
        +addProduct(Product) void
        +findById(int) Optional~Product~
        +findAll() List~Product~
    }
}

namespace service {
    class VendingMachine {
        <<interface>>
        +insertCoin(int) boolean
        +purchaseProduct(int) Product
        +returnChange() int
        +getBalance() int
        +getProducts() List~Product~
    }

    class VendingMachineImpl {
        -int balance
        -ProductRepository repository
        -Set~Integer~ acceptedCoins
        +VendingMachineImpl(repository: ProductRepository)
        +insertCoin(int) boolean
        +purchaseProduct(int) Product
        +returnChange() int
        +getBalance() int
        +getProducts() List~Product~
    }
}

class ConsoleUI {
    -VendingMachine vendingMachine
    +start() void
    -displayProducts() void
    -displayBalance() void
}

class Main {
    +main(String[]) void
}

Product <|-- Snack
Product <|-- Beverage
Product <|-- Fruit

ProductRepository <|.. InMemoryProductRepository
VendingMachine <|.. VendingMachineImpl

VendingMachineImpl --> "1" ProductRepository : uses
InMemoryProductRepository "1" o-- "0..*" Product : stores
ConsoleUI --> "1" VendingMachine : uses

```

> `Main` is intentionally isolated. It is an **entry point only**: it instantiates `VendingMachineImpl` and `ConsoleUI`, wires them together, and calls `start()`.

