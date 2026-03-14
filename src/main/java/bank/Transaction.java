package bank;

/**
 * Abstrakte Oberklasse für alle Transaktionen.
 * <p>
 * Enthält gemeinsame Attribute wie Betrag, Datum und Beschreibung.
 * Wird von {@link Payment} und {@link Transfer} erweitert.
 */

public abstract class Transaction implements CalculateBill{
    /** Betrag der Transaktion (kann positiv oder negativ sein). */
    protected double amount;

    /** Datum der Transaktion im Format YYYY-MM-DD. */
    protected String date;
    /** Beschreibung oder Verwendungszweck. */
    protected String description;
    /**
     * Erstellt eine neue Transaktion.
     *
     * @param amount      Betrag der Transaktion
     * @param date        Datum der Transaktion
     * @param description Beschreibung der Transaktion
     */
    public Transaction(double amount, String date, String description) {
        setAmount(amount);
        this.date = date;
        this.description = description;
    }
    /**
     * Liefert den Betrag.
     *
     * @return Betrag der Transaktion
     */
    public double getAmount() {
        return amount;
    }
    /** Setzt den Betrag. */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    /** @return Datum der Transaktion */
    public String getDate() {
        return date;
    }

    /** Setzt das Datum. */
    public void setDate(String date) {
        this.date = date;

    }
    /** @return Beschreibung der Transaktion */
    public String getDescription() {
        return description;
    }
    /** Setzt die Beschreibung. */
    public void setDescription(String description) {
        this.description = description;
    }
    /** @return Textdarstellung der Transaktion */
    @Override
    public String toString(){
        return "Transaction{date='" + date + "', description='" + this.description + "', amount=" + getAmount() + "}";
    }
    /** Vergleicht zwei Transaktionen auf Gleichheit. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction t = (Transaction) o;
        return this.date.equals(t.date)&& this.amount ==t.amount && this.description.equals(t.description);
    }
    public Transaction(Transaction t) {
        this.amount = t.getAmount();
        this.date = t.getDate();
        this.description = t.getDescription();
    }


}
