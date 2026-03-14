# Bank Application (P5)

A JavaFX banking application developed for the Object-Oriented Software course at FH Aachen.

## Features

- Manage bank accounts
- Perform payments (deposits and withdrawals)
- Handle incoming and outgoing transfers between accounts
- Persistent storage of account data as JSON files
- GUI built with JavaFX

## Technologies

- Java 17
- JavaFX 22
- Gson 2.11 (JSON serialization)
- JUnit Jupiter (testing)
- Maven

## How to Run

```bash
mvn javafx:run
```

## How to Test

```bash
mvn test
```

## Project Structure

```
src/
├── main/java/
│   ├── bank/          # Core banking logic (Bank, Payment, Transfer, ...)
│   ├── ui/            # JavaFX controllers
│   └── Main.java
├── main/resources/ui/ # FXML views
└── test/java/bank/    # Unit tests
```