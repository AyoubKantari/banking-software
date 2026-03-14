package bank;

import bank.exceptions.AccountAlreadyExistsException;
import bank.exceptions.AccountDoesNotExistException;
import bank.exceptions.TransactionAlreadyExistException;
import bank.exceptions.TransactionDoesNotExistException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PrivateBankTest {

    private PrivateBank bank;
    private String testDir = "testdata";
    private Payment payment1;
    private Payment payment2;
    private Transfer transfer1;
    private Transfer transfer2;
    private OutgoingTransfer outgoingTransfer;

    @BeforeEach
    void setUp() throws IOException {

        bank = new PrivateBank("TestBank", 0.1, 0.2, testDir);

        payment1 = new Payment(100, "2024-01-15", "Gehalt", 0.1, 0.2);
        payment2 = new Payment(-50, "2024-01-16", "Ausgabe", 0.1, 0.2);
        transfer1 = new IncomingTransfer(200, "2024-01-17", "Überweisung",
                "Ayoub", "Kantari");
        transfer2  = new Transfer(100,"10.20.2002","überweisung", "Ayoub", "Kantari");
        outgoingTransfer = new OutgoingTransfer(100,"20.10.2002","Überweisung","Ayoub","Kantari");

    }

    @AfterEach
    void tearDown() {
        // Wird nach JEDEM Test ausgeführt
        // Lösche alle Test-Dateien
        deleteDirectory(new File(testDir));
    }

    /**
     * Hilfsmethode zum Löschen eines Verzeichnisses mit allen Dateien
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    //  KONSTRUKTOR TEST

    @Test

    void testConstructor() throws Exception {
        // SCHRITT 1: Erstelle Bank und Konto
        PrivateBank bank1 = new PrivateBank("Bank1", 0.1, 0.2, testDir);
        bank1.createAccount("Ayoub");
        bank1.addTransaction("Ayoub", payment1);

        // SCHRITT 2: Erstelle NEUE Bank mit GLEICHEM Directory
        PrivateBank bank2 = new PrivateBank("Bank2", 0.1, 0.2, testDir);

        // SCHRITT 3: Prüfe dass Konto geladen wurde
        List<Transaction> transactions = bank2.getTransactions("Ayoub");
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
    }

    //  CREATE ACCOUNTTESTS

    @Test

    void testCreateAccountWorks() throws Exception {
        assertDoesNotThrow(() -> {
            bank.createAccount("Ayoub");
        });

        List<Transaction> transactions = bank.getTransactions("Ayoub");
        assertNotNull(transactions);
        assertEquals(0, transactions.size());  // Leer aber existiert
    }
   // wirft AccountAlreadyExistsException
    @Test
    void testCreateAccountThrowsException() throws Exception {
        bank.createAccount("Ayoub");

        // Zweites Mal sollte Exception werfen
        assertThrows(AccountAlreadyExistsException.class, () -> {
            bank.createAccount("Ayoub");
        });
    }

    @Test
    void testCreateAccountmitTransactions() throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(payment1);
        transactions.add(payment2);

        assertDoesNotThrow(() -> {
            bank.createAccount("Kantari", transactions);
        });

        List<Transaction> loaded = bank.getTransactions("Kantari");
        assertEquals(2, loaded.size());
    }

    // ==================== ADD TRANSACTION TESTS ====================

    @Test
    @DisplayName("addTransaction() fügt Transaktion hinzu")
    void testAddTransactionWorks() throws Exception {
        bank.createAccount("Ayoub");

        assertDoesNotThrow(() -> {
            bank.addTransaction("Ayoub", payment1);
        });

        List<Transaction> transactions = bank.getTransactions("Ayoub");
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(payment1));
    }
    @Test
    @DisplayName("addTransaction() fügt Transaktion hinzu")
    void testAddTransactionWorks2() throws Exception {
        bank.createAccount("Ayoub");

        assertDoesNotThrow(() -> {
            bank.addTransaction("Ayoub", transfer2);
        });

        List<Transaction> transactions = bank.getTransactions("Ayoub");
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(transfer2));
    }
    @Test
    @DisplayName("addTransaction() fügt Transaktion hinzu")
    void testAddTransactionWorks3() throws Exception {
        bank.createAccount("Ayoub");

        assertDoesNotThrow(() -> {
            bank.addTransaction("Ayoub", outgoingTransfer);
        });

        List<Transaction> transactions = bank.getTransactions("Ayoub");
        assertEquals(1, transactions.size());
        assertTrue(transactions.contains(outgoingTransfer));
    }


    @Test

    void testAddTransactionThrowsAccountDoesNotExist() {
        // Konto existiert nicht
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.addTransaction("Unbekannt", payment1);
        });
    }

    @Test
    @DisplayName("addTransaction() wirft TransactionAlreadyExistException")
    void testAddTransactionThrowsTransactionAlreadyExists() throws Exception {
        bank.createAccount("Ayoub");
        bank.addTransaction("Ayoub", payment1);

        // Gleiche Transaktion nochmal hinzufügen
        assertThrows(TransactionAlreadyExistException.class, () -> {
            bank.addTransaction("Ayoub", payment1);
        });
    }

    // ==================== REMOVE TRANSACTION TESTS ====================

    @Test
    @DisplayName("removeTransaction() entfernt Transaktion")
    void testRemoveTransactionWorks() throws Exception {
        bank.createAccount("Ayoub");
        bank.addTransaction("Ayoub", payment1);

        assertDoesNotThrow(() -> {
            bank.removeTransaction("Ayoub", payment1);
        });

        List<Transaction> transactions = bank.getTransactions("Ayoub");
        assertEquals(0, transactions.size());
        assertFalse(bank.containsTransaction("Ayoub", payment1));
    }
  // removeTransaction wirft AccountDoesNotExistException
    @Test
    void testRemoveTransactionThrowsAccountDoesNotExist() {
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.removeTransaction("Unbekannt", payment1);
        });
    }
  //removeTransaction() wirft TransactionDoesNotExistException
    @Test

    void testRemoveTransactionThrowsTransactionDoesNotExist() throws Exception {
        bank.createAccount("Ayoub");

        assertThrows(TransactionDoesNotExistException.class, () -> {
            bank.removeTransaction("Ayoub", payment1);
        });
    }

    //  CONTAINS TRANSACTION TEST
    // containsTransaction() prüft Existenz
    @Test

    void testContainsTransaction() throws Exception {
        bank.createAccount("Ayoub");

        // Vor dem Hinzufügen
        assertFalse(bank.containsTransaction("Ayoub", payment1));


        // Nach dem Hinzufügen
        bank.addTransaction("Ayoub", payment1);

        assertTrue(bank.containsTransaction("Ayoub", payment1));
        bank.addTransaction("Ayoub", outgoingTransfer);
        assertTrue(bank.containsTransaction("Ayoub", outgoingTransfer));
        // Nach dem Entfernen
        bank.removeTransaction("Ayoub", payment1);
        assertFalse(bank.containsTransaction("Ayoub", payment1));
    }



    //  GET ACCOUNT BALANCE TEST

    @Test
    //getAccountBalance() berechnet Kontostand korrekt

    void testGetAccountBalance() throws Exception {
        bank.createAccount("Ayoub");

        // Payment: 100 - (100 * 0.1) = 90
        bank.addTransaction("Ayoub", new Payment(100, "2024-01-15", "Test", 0.1, 0.2));

        // Payment: -50 * (1 + 0.2) = -60
        bank.addTransaction("Ayoub", new Payment(-50, "2024-01-16", "Test2", 0.1, 0.2));

        double balance = bank.getAccountBalance("Ayoub");

        // 90 + (-60) = 30
        assertEquals(30.0, balance, 0.001);
    }

    @Test
    //getAccountBalance() wirft AccountDoesNotExistException
    void testGetAccountBalanceThrowsException() {
        assertThrows(AccountDoesNotExistException.class, () -> {
            bank.getAccountBalance("Unbekannt");
        });
    }

    //  GET TRANSACTIONS TEST

    @Test
    //getTransactions() gibt alle Transaktionen zurück"
    void testGetTransactions() throws Exception {
        bank.createAccount("Ayoub");
        bank.addTransaction("Ayoub", payment1);
        bank.addTransaction("Ayoub", payment2);
        bank.addTransaction("Ayoub", transfer1);

        List<Transaction> transactions = bank.getTransactions("Ayoub");

        assertEquals(3, transactions.size());
        assertTrue(transactions.contains(payment1));
        assertTrue(transactions.contains(payment2));
        assertTrue(transactions.contains(transfer1));
    }

    // GET TRANSACTIONS SORTED TEST

    @Test
   //getTransactionsSorted() sortiert aufsteigend
    void testGetTransactionsSortedAscending() throws Exception {
        bank.createAccount("Ayoub");

        Payment p1 = new Payment(100, "2024-01-15", "Test", 0.1, 0.2);  // = 90
        Payment p2 = new Payment(-50, "2024-01-16", "Test", 0.1, 0.2);  // = -60
        Payment p3 = new Payment(200, "2024-01-17", "Test", 0.1, 0.2);  // = 180

        bank.addTransaction("Ayoub", p1);
        bank.addTransaction("Ayoub", p2);
        bank.addTransaction("Ayoub", p3);

        List<Transaction> sorted = bank.getTransactionsSorted("Ayoub", true);

        assertEquals(-60.0, sorted.get(0).calculate(), 0.001);
        assertEquals(90.0, sorted.get(1).calculate(), 0.001);
        assertEquals(180.0, sorted.get(2).calculate(), 0.001);
    }

    @Test
    //getTransactionsSorted() sortiert absteigend
    void testGetTransactionsSortedDescending() throws Exception {
        bank.createAccount("Ayoub");

        Payment p1 = new Payment(100, "2024-01-15", "Test", 0.1, 0.2);  // = 90
        Payment p2 = new Payment(-50, "2024-01-16", "Test", 0.1, 0.2);  // = -60
        Payment p3 = new Payment(200, "2024-01-17", "Test", 0.1, 0.2);  // = 180

        bank.addTransaction("Ayoub", p1);
        bank.addTransaction("Ayoub", p2);
        bank.addTransaction("Ayoub", p3);

        List<Transaction> sorted = bank.getTransactionsSorted("Ayoub", false);

        assertEquals(180.0, sorted.get(0).calculate(), 0.001);
        assertEquals(90.0, sorted.get(1).calculate(), 0.001);
        assertEquals(-60.0, sorted.get(2).calculate(), 0.001);
    }

    // GET TRANSACTIONS BY TYPE TEST

    @Test
    //getTransactionsByType() filtert positive Transaktionen
    void testGetTransactionsByTypePositive() throws Exception {
        bank.createAccount("Ayoub");

        Payment p1 = new Payment(100, "2024-01-15", "Test", 0.1, 0.2);  // = 90 (positiv)
        Payment p2 = new Payment(-50, "2024-01-16", "Test", 0.1, 0.2);  // = -60 (negativ)
        Payment p3 = new Payment(200, "2024-01-17", "Test", 0.1, 0.2);  // = 180 (positiv)

        bank.addTransaction("Ayoub", p1);
        bank.addTransaction("Ayoub", p2);
        bank.addTransaction("Ayoub", p3);

        List<Transaction> positive = bank.getTransactionsByType("Ayoub", true);

        assertEquals(2, positive.size());
        assertTrue(positive.contains(p1));
        assertTrue(positive.contains(p3));
        assertFalse(positive.contains(p2));
    }

    @Test
    //getTransactionsByType() filtert negative Transaktionen
    void testGetTransactionsByTypeNegative() throws Exception {
        bank.createAccount("Ayoub");

        Payment p1 = new Payment(100, "2024-01-15", "Test", 0.1, 0.2);  // = 90 (positiv)
        Payment p2 = new Payment(-50, "2024-01-16", "Test", 0.1, 0.2);  // = -60 (negativ)
        OutgoingTransfer p3 = new OutgoingTransfer(100, "2024-01-17", "Test",
                "Ayoub", "Kantari");  // = -100 (negativ)

        bank.addTransaction("Ayoub", p1);
        bank.addTransaction("Ayoub", p2);
        bank.addTransaction("Ayoub", p3);

        List<Transaction> negative = bank.getTransactionsByType("Ayoub", false);

        assertEquals(2, negative.size());
        assertTrue(negative.contains(p2));
        assertTrue(negative.contains(p3));
        assertFalse(negative.contains(p1));
    }

    // EQUALS TEST

    @Test
    //equals() vergleicht zwei Banken
    void testEquals() throws Exception {
        PrivateBank bank1 = new PrivateBank("Bank", 0.1, 0.2, "test1");
        PrivateBank bank2 = new PrivateBank("Bank", 0.1, 0.2, "test2");

        bank1.createAccount("Ayoub");
        bank2.createAccount("Ayoub");

        bank1.addTransaction("Ayoub", payment1);
        bank2.addTransaction("Ayoub", payment1);

        assertEquals(bank1, bank2);

        // Cleanup
        deleteDirectory(new File("test1"));
        deleteDirectory(new File("test2"));
    }

    @Test
   //equals() erkennt unterschiedliche Banken
    void testNotEquals() throws Exception {
        PrivateBank bank1 = new PrivateBank("Bank1", 0.1, 0.2, "test1");
        PrivateBank bank2 = new PrivateBank("Bank2", 0.1, 0.2, "test2");

        assertNotEquals(bank1, bank2);

        // Cleanup
        deleteDirectory(new File("test1"));
        deleteDirectory(new File("test2"));
    }

    // TOSTRING TEST

    @Test
    //toString() enthält alle wichtigen Informationen
    void testToString() {
        String result = bank.toString();

        assertTrue(result.contains("TestBank"));
        assertTrue(result.contains("0.1"));
        assertTrue(result.contains("0.2"));
    }
}