package bank;

import bank.exceptions.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
/**
 * Repräsentiert eine private Bank mit mehreren Konten und deren Transaktionen.
 * <p>
 * Für jedes Konto wird eine Liste von {@link Transaction}-Objekten gespeichert.
 * Die Bank verwaltet außerdem bankweite Zinssätze, die bei {@link Payment}-Transaktionen
 * verwendet werden.
 * </p>
 */

public class PrivateBank implements Bank {
    private String name;
    private double incomingInterest;
    private double outgoingInterest;
    private String directoryName;
    private Gson gson;
    private  Map<String, List<Transaction>> accountsToTransactions = new HashMap<>();



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setIncomingInterest(double inc) {
        if (inc < 0 || inc > 1) {
            System.out.println("Incoming interest out of range");
        }
        this.incomingInterest = inc;
    }

    public double getIncomingInterest() {
        return this.incomingInterest;
    }

    public void setOutgoingInterest(double out) {
        if (out < 0 || out > 1) {
            System.out.println("Outgoing interest out of range");
        }
        this.outgoingInterest = out;
    }
    public String getDirectoryName() {
        return this.directoryName;
    }
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
    public double getOutgoingInterest() {
        return this.outgoingInterest;
    }
    /**
     * Konstruktor zum Anlegen einer neuen Bank mit Namen und Zinssätzen.
     *
     * @param n   Name der Bank
     * @param in  Zinssatz für eingehende Zahlungen im Bereich [0,1].
     *            Wird bei {@link Payment}-Transaktionen auf positive Beträge angewendet.
     * @param out Zinssatz für ausgehende Zahlungen im Bereich [0,1].
     *            Wird bei {@link Payment}-Transaktionen auf negative Beträge angewendet.
     * @param des Name des Verzeichnisses, in dem die JSON-Dateien der Konten gespeichert werden.
     *            Jedes Konto erhält eine eigene Datei im Format {@code <account>.json}.
     *
     * @throws IOException falls beim Lesen existierender Konto-Dateien ein Fehler auftritt
     *                     oder das Verzeichnis nicht erstellt werden kann
     *
     * @see #readAccounts()
     * @see TransactionAdapter
     */

    public PrivateBank(String n, double in, double out, String des) throws IOException {
        setName(n);
        setIncomingInterest(in);
        setOutgoingInterest(out);
        setDirectoryName(des);

        this.gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionAdapter()).setPrettyPrinting()
                .create();

        // NEU: Verzeichnis erstellen falls nicht vorhanden
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Bereits vorhandene Konten aus JSON laden
        readAccounts();
    }
    /**
     * Kopierkonstruktor.
     * @param other die zu kopierende {@code PrivateBank}-Instanz
     */
    public PrivateBank(PrivateBank other) {
        this.name = other.name;
        this.incomingInterest = other.incomingInterest;
        this.outgoingInterest = other.outgoingInterest;
    }


    @Override
    public String toString() {
        return "PrivateBank{" +
                "name='" + name + '\'' +
                ", incomingInterest=" + incomingInterest +
                ", outgoingInterest=" + outgoingInterest +
                ", directoryName='" + directoryName + '\'' +
                ", accountsToTransactions=" + accountsToTransactions +
                '}';
    }
    /**
     * Vergleicht diese Bank mit einem anderen Objekt auf Gleichheit.
     *
     * @param o das zu vergleichende Objekt
     * @return {@code true}, wenn beide Objekte gleich sind, sonst {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != getClass()) return false;
        PrivateBank other = (PrivateBank) o;
        return Objects.equals(this.name, other.name)
                && Double.compare(this.incomingInterest, other.incomingInterest) == 0
                && Double.compare(this.outgoingInterest, other.outgoingInterest) == 0
                && Objects.equals(this.accountsToTransactions, other.accountsToTransactions);
    }
    /**
     * Lädt alle existierenden Konten aus dem verzeichnis
     *Diese Methode durchsucht das konfigurierte Verzeichnis nach JSON-Dateien und
     * deserialisiert die darin enthaltenen Transaktions-Listen. Für jede gefundene
     * Datei wird ein Konto in accountsToTransactions angelegt
     *
     * @throws IOException falls beim Lesen der JSON-Dateien ein Fehler auftritt
     *
     *
     */
    private void readAccounts() throws IOException {
        File directory = new File(directoryName);
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null) {
            return;  // Kein Fehler, einfach keine Dateien
        }

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                Type transactionListType = new TypeToken<List<Transaction>>(){}.getType();
                List<Transaction> transactions = gson.fromJson(reader, transactionListType);

                String accountName = file.getName().replace(".json", "");
                accountsToTransactions.put(accountName, transactions);
            }
        }
    }
    /**
     * Speichert alle Transaktionen eines Kontos persistent in einer JSON-Date
     *
     * Diese Methode serialisiert die komplette Transaktions-Liste in eine JSON-Datei im Format
     * Die Serialisierung erfolgt mittels Gson und dem registrierte
     * Die Methode erstellt das Verzeichnis automatisch, falls es nicht existiert,
     *
     * @param account Name des Kontos, dessen Transaktionen gespeichert werden sollen
     *
     * @throws IOException falls beim Schreiben der JSON-Datei ein Fehler auftritt
     */
    private void writeAccount(String account) throws IOException {
        if (directoryName == null) {
            return;
        }

        File dir = new File(directoryName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<Transaction> list = accountsToTransactions.get(account);
        if (list == null) {
            return;
        }

        File file = new File(dir, account + ".json");

        try (Writer writer = new FileWriter(file)) {
            Type listType = new TypeToken<List<Transaction>>() {}.getType();
            gson.toJson(list, listType, writer);
        }
    }

    /**
     * Legt ein neues Konto ohne Anfangstransaktionen an.
     *
     * @param account Name des neuen Kontos
     * @throws AccountAlreadyExistsException falls bereits ein Konto mit diesem Namen existiert
     */





    @Override
    public void createAccount(String account) throws AccountAlreadyExistsException, IOException {
        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }


        accountsToTransactions.put(account, new ArrayList<>());
         writeAccount(account);


    }
    /**
     * Legt ein neues Konto mit einer Liste von Anfangstransaktionen an.
     * @param account      Name des neuen Kontos
     * @param transactions Liste von Transaktionen, die dem Konto zugeordnet werden sollen; darf {@code null} sein
     * @throws AccountAlreadyExistsException     falls bereits ein Konto mit diesem Namen existiert
     * @throws TransactionAlreadyExistException falls in der übergebenen Liste für dieses Konto doppelte Transaktionen enthalten sind
     * @throws TransactionAttributeException     falls ungültige Attributwerte in den Transaktionen erkannt werden (z.B. negativer Transferbetrag
     *                                           oder ungültige Zinssätze)
     */
    @Override
    public void createAccount(String account, List<Transaction> transactions)
            throws AccountAlreadyExistsException, TransactionAlreadyExistException, TransactionAttributeException, IOException {

        if (accountsToTransactions.containsKey(account)) {
            throw new AccountAlreadyExistsException("Account already exists: " + account);
        }

        List<Transaction> accountTransactions = new ArrayList<Transaction>();

        if (transactions != null) {
            for (Transaction t : transactions) {

                // doppelte Transaktion verhindern
                if (accountTransactions.contains(t)) {

                    throw new TransactionAlreadyExistException("Transaction already exists for this account: " + t);
                }

                // Attribut-Prüfung: Transfer amount >= 0
                if (t instanceof Transfer) {
                    Transfer transfer = (Transfer) t;
                    if (transfer.getAmount() < 0) {
                        throw new TransactionAttributeException("Transfer amount must be >= 0");
                    }
                }

                // Attribut-Prüfung + Zinsübernahme bei Payment
                if (t instanceof Payment) {
                    if (incomingInterest < 0 || incomingInterest > 1
                            || outgoingInterest < 0 || outgoingInterest > 1) {
                        throw new TransactionAttributeException("Interest rates must be between 0 and 1");
                    }

                    Payment payment = (Payment) t;
                    // Bank-Zinsen überschreiben Payment-Zinsen
                    payment.setIncomingInterest(incomingInterest);
                    payment.setOutgoingInterest(outgoingInterest);
                }

                accountTransactions.add(t);
            }
        }

        accountsToTransactions.put(account, accountTransactions);
        writeAccount(account);
    }
    /**
     * Fügt einem bestehenden Konto eine neue Transaktion hinzu.
     * @param account     Name des Kontos
     * @param transaction hinzuzufügende Transaktion
     * @throws TransactionAlreadyExistException falls die Transaktion bereits für dieses Konto existiert
     * @throws AccountDoesNotExistException      falls das Konto nicht existiert
     * @throws TransactionAttributeException     falls ungültige Attributwerte in der Transaktion erkannt werden
     */
    @Override
    public void addTransaction(String account, Transaction transaction)
            throws TransactionAlreadyExistException, AccountDoesNotExistException, TransactionAttributeException, IOException {

        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }


        List<Transaction> accountTransactions = accountsToTransactions.get(account);

        // 3. Transaktion darf nicht schon vorhanden sein
        if (accountTransactions.contains(transaction)) {
            throw new TransactionAlreadyExistException("Transaction already exists for this account: " + transaction);
        }

        if (transaction instanceof Transfer) {
            Transfer transfer = (Transfer) transaction;
            if (transfer.getAmount() < 0) {
                throw new TransactionAttributeException("Transfer amount must be >= 0");
            }
        }

        if (transaction instanceof Payment) {
            if (incomingInterest < 0 || incomingInterest > 1
                    || outgoingInterest < 0 || outgoingInterest > 1) {
                throw new TransactionAttributeException("Interest rates must be between 0 and 1");
            }

            Payment payment = (Payment) transaction;
            payment.setIncomingInterest(incomingInterest);
            payment.setOutgoingInterest(outgoingInterest);
        }

        accountTransactions.add(transaction);
        writeAccount(account);
    }
    /**
     * Entfernt eine Transaktion von einem Konto.
     * @param account     Name des Kontos
     * @param transaction zu entfernende Transaktion
     * @throws AccountDoesNotExistException      falls das Konto nicht existiert
     * @throws TransactionDoesNotExistException  falls die Transaktion nicht in der Liste des Kontos enthalten ist
     */
    public  void removeTransaction(String account, Transaction transaction)
            throws AccountDoesNotExistException, TransactionDoesNotExistException, IOException {
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }
        List<Transaction> accountList = accountsToTransactions.get(account);
        if (!accountList.contains(transaction)) {
            throw new TransactionDoesNotExistException("Transaction does not exist: " + transaction);
        }
        accountList.remove(transaction);
        writeAccount(account);
    }
    /**
     * Prüft, ob eine bestimmte Transaktion bei einem Konto existiert.
     *
     * @param account     Name des Kontos
     * @param transaction gesuchte Transaktion
     * @return {@code true}, wenn das Konto existiert und die Transaktion in der Liste enthalten ist, sonst {@code false}
     */
    @Override
   public boolean containsTransaction(String account, Transaction transaction){
        return accountsToTransactions.containsKey(account) && accountsToTransactions.get(account).contains(transaction);
    }
    /**
     * Berechnet den aktuellen Kontostand eines Kontos.
     * @param account Name des Kontos
     * @return aktueller Kontostand
     */
    @Override
    public double getAccountBalance(String account) throws AccountDoesNotExistException {
        if (!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }

        List<Transaction> transactions = accountsToTransactions.get(account);
        double sum = 0.0;

        // Keine Unterscheidung
        for (Transaction t : transactions) {
            sum += t.calculate();
        }

        return sum;
    }
    /**
     * Liefert alle Transaktionen eines Kontos als neue Liste.
     * <p>
     * Wird ein unbekanntes Konto angegeben, wird eine leere Liste zurückgegeben.
     * Die interne Datenstruktur der Bank wird durch eine Kopie geschützt.
     * </p>
     *
     * @param account Name des Kontos
     * @return neue Liste mit allen Transaktionen des Kontos (nie {@code null})
     */
    @Override
    public List<Transaction> getTransactions(String account) {
        List<Transaction> accountTransactions = accountsToTransactions.get(account);

        return accountTransactions;
    }
    /**
     * Liefert alle Transaktionen eines Kontos sortiert nach ihrem berechneten Wert.
     * <p>
     * Die Sortierung erfolgt auf Basis von {@link Transaction#calculate()}.
     * </p>
     * @param account Name des Kontos
     * @param asc     {@code true} für aufsteigende, {@code false} für absteigende Sortierung
     * @return neue, sortierte Liste der Transaktionen (nie {@code null})
     */
    public List<Transaction> getTransactionsSorted(String account, boolean asc){
        List<Transaction> list= getTransactions(account);

        if (list == null) return new ArrayList<>();

        // Mache eine Kopie der Liste, um die Originaldaten nicht zu verändern
        List<Transaction> sortedList = new ArrayList<>(list);


        if (asc) {
            sortedList.sort((a, b) -> Double.compare(a.calculate(), b.calculate()));
        } else {
            sortedList.sort((a, b) -> Double.compare(b.calculate(), a.calculate()));
        }

        return sortedList;
    }

    /**
     * Filtert die Transaktionen eines Kontos nach ihrem Vorzeichen.
     * @param account  Name des Kontos
     * @param positive {@code true}, um nur Transaktionen mit {@code calculate() >= 0}
     *                 zu erhalten, {@code false} für Transaktionen mit {@code calculate() < 0}
     * @return Liste der gefilterten Transaktionen (nie {@code null})
     */
    @Override
    public List<Transaction> getTransactionsByType(String account, boolean positive) {
        List<Transaction> result = new ArrayList<Transaction>();
        List<Transaction> accountTransactions = accountsToTransactions.get(account);

        if (accountTransactions == null) {
            return result;
        }

        for (Transaction transaction : accountTransactions) {
            double value = transaction.calculate();
            if (positive) {
                if (value >= 0) {
                    result.add(transaction);
                }
            } else {
                if (value < 0) {
                    result.add(transaction);
                }
            }
        }

        return result;
    }
    @Override
    public void deleteAccount(String account)
            throws AccountDoesNotExistException, IOException {

        // 1. Prüfen ob Account existiert
        if(!accountsToTransactions.containsKey(account)) {
            throw new AccountDoesNotExistException("Account does not exist: " + account);
        }

        accountsToTransactions.remove(account);

        // 3. JSON-Datei löschen
        File file = new File(directoryName, account + ".json");  // ← KORREKTUR!

        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Datei konnte nicht gelöscht werden: " + file.getPath());
            }
        }

    }

    @Override
    public List<String> getAllAccounts() {
        return new ArrayList<>(accountsToTransactions.keySet());
    }
}
