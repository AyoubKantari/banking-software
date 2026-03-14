import bank.*;
import bank.exceptions.*;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;
import java.util.*;




public class Main {

    public static void main(String[] args) {

        System.out.println("=== Test der Basisklassen Payment & Transfer ===");

        Payment payment1 = new Payment(500, "28.10.2025", "Einzahlung", 0.05, 0.10);
        Payment payment2 = new Payment(-100, "28.10.2025", "Auszahlung", 0.05, 0.10);
        Payment payment3 = new Payment(500,"28.10.2025", "Andere Einzahlung",  1, 1);

        Transfer transfer1 = new Transfer(500,"28.10.2025", "Überweisung",  "Ali", "Youssef");
        Transfer transfer2 = new Transfer(200,"28.10.2025", "Überweisung",  "Ilyas", "Ali");

        Payment copy1 = new Payment(payment1);
        Transfer copy2 = new Transfer(transfer1);

        System.out.println("\nEinzahlung:\n" + payment1);
        System.out.println("calculate(): " + payment1.calculate());

        System.out.println("\nAuszahlung:\n" + payment2);
        System.out.println("calculate(): " + payment2.calculate());

        System.out.println("\nTransfer:\n" + transfer1);
        System.out.println("calculate(): " + transfer1.calculate());

        System.out.println("\nKopie Payment:\n" + copy1);
        System.out.println("\nKopie Transfer:\n" + copy2);

        System.out.println("\nEquals-Tests:");
        System.out.println("payment1 equals payment3? " + payment1.equals(payment3));
        System.out.println("transfer1 equals transfer2? " + transfer1.equals(transfer2));

        // ---------------------------------------------------------------
        // Test PrivateBank & PrivateBankAlt
        // ---------------------------------------------------------------
        System.out.println("\n\n=== Tests für PrivateBank und PrivateBankAlt ===");


        try {

            // PRAKTIKUM 4: constructor now requires directoryName
            PrivateBank bank1 = new PrivateBank("MeineBank", 0.05, 0.10, "bank1_accounts");
            PrivateBank bank2 = new PrivateBank("MeineBank", 0.05, 0.10, "bank2_accounts");
            PrivateBank bank3 = new PrivateBank("AndereBank", 0.05, 0.10, "bank3_accounts");

            // PrivateBankAlt also requires directoryName


            // Konto anlegen
            bank1.createAccount("Konto1");


            // Konto mit Startliste
            List<Transaction> startList = new ArrayList<>();
            startList.add(new Payment(50.0,"21.10.2025", "Startzahlung",  0.05, 0.10));
            bank1.createAccount("KontoMitListe", startList);

            // Fehlerfall: AccountAlreadyExistsException
            try {
                bank1.createAccount("Konto1");
            } catch (AccountAlreadyExistsException e) {
                System.out.println("AccountAlreadyExistsException (Konto1 existiert): " + e.getMessage());
            }

            Payment payPos = new Payment(100.0,"22.11.2025", "Einzahlung",  0.05, 0.10);
            Payment payNeg = new Payment(-20.0,"23.11.2025", "Auszahlung",  0.05, 0.10);

            OutgoingTransfer sendBank1 = new OutgoingTransfer(30.0,"24.11.2025", "Sendung",  "Konto1", "KontoZ");
            IncomingTransfer recvBank1 = new IncomingTransfer(40.0,"25.11.2025", "Empfang",  "KontoY", "Konto1");

            Transfer sendAlt = new Transfer(30.0,"24.11.2025", "Sendung",  "Konto1", "KontoZ");
            Transfer recvAlt = new Transfer(40.0, "25.10.2025", "Empfang", "KontoY", "Konto1");

            bank1.addTransaction("Konto1", payPos);
            bank1.addTransaction("Konto1", payNeg);
            bank1.addTransaction("Konto1", sendBank1);
            bank1.addTransaction("Konto1", recvBank1);


            System.out.println("\nTransaktionen von Konto1 (bank1):");
            for (Transaction t : bank1.getTransactions("Konto1")) {
                System.out.println(t + " -> calculate(): " + t.calculate());
            }

            System.out.println("\nKontostand Konto1 (bank1): " + bank1.getAccountBalance("Konto1"));


            System.out.println("\nSortiert (ascending):");
            for (Transaction t : bank1.getTransactionsSorted("Konto1", true)) {
                System.out.println(t + " -> calculate(): " + t.calculate());
            }

            System.out.println("\nPositive Transaktionen:");
            for (Transaction t : bank1.getTransactionsByType("Konto1", true)) {
                System.out.println(t + " -> calculate(): " + t.calculate());
            }

            System.out.println("\nNegative Transaktionen:");
            for (Transaction t : bank1.getTransactionsByType("Konto1", false)) {
                System.out.println(t + " -> calculate(): " + t.calculate());
            }

            System.out.println("\nbank1 enthält payPos? " + bank1.containsTransaction("Konto1", payPos));

            bank1.removeTransaction("Konto1", payNeg);
            System.out.println("Kontostand nach remove: " + bank1.getAccountBalance("Konto1"));

            try {
                bank1.removeTransaction("Konto1", payNeg);
            } catch (TransactionDoesNotExistException e) {
                System.out.println("TransactionDoesNotExistException: " + e.getMessage());
            }

            try {
                bank1.addTransaction("Unbekannt", payPos);
            } catch (AccountDoesNotExistException e) {
                System.out.println("AccountDoesNotExistException: " + e.getMessage());
            }



            // Fehlerhafte Zinsen testen
            PrivateBank wrongBank = new PrivateBank("WrongBank", -0.5, 2.0, "wrong_accounts");
            wrongBank.createAccount("Fehler");
            try {
                wrongBank.addTransaction("Fehler",
                        new Payment(50,"27.11.2025", "ungültig",  0.05, 0.10));
            } catch (TransactionAttributeException e) {
                System.out.println("TransactionAttributeException (wrongBank): " + e.getMessage());
            }

            // equals Tests
            System.out.println("\n=== equals()-Tests ===");
            System.out.println("bank1 equals bank2? " + bank1.equals(bank2));
            System.out.println("bank1 equals bank3? " + bank1.equals(bank3));


        } catch (Exception e) {
            System.out.println("Unerwarteter Fehler: " + e.getMessage());
        }

    }
}

