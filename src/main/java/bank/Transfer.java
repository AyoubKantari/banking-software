package bank;

import java.util.Objects;

/**
 * Repräsentiert eine Überweisung (Transfer) zwischen zwei Konten.
 * <p>
 * Diese Klasse erbt von {@link Transaction} und implementiert das Interface
 * {@link CalculateBill}. Bei einer Überweisung fallen keine Zinsen an, daher
 * wird der Betrag unverändert zurückgegeben.
 */
public class Transfer extends Transaction  {

    /** Name oder Kennung des Senders (Absenderkonto). */
    private String sender;

    /** Name oder Kennung des Empfängers (Empfängerkonto). */
    private String receiver;



    @Override
    public void setAmount(double a){
        if (a<0){
            System.out.println("Amount is negative");
        }
        else{
            super.setAmount(a);
        }
    }

    /**

     * Erstellt ein neues {@code Transfer}-Objekt.
     *
     * @param a           Betrag der Überweisung
     * @param date        Datum der Transaktion
     * @param description Beschreibung oder Verwendungszweck
     * @param sender      Absender der Überweisung
     * @param receiver    Empfänger der Überweisung
     */
    public Transfer(double a, String date, String description, String sender, String receiver) {
        super(a, date, description);
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Liefert den Namen bzw. die Kennung des Senders.
     *
     * @return Sender der Überweisung
     */
    public String getSender() {
        return sender;
    }

    /**
     * Setzt den Sender der Überweisung.
     *
     * @param sender neuer Sendername oder -kennung
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Liefert den Namen bzw. die Kennung des Empfängers.
     *
     * @return Empfänger der Überweisung
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Setzt den Empfänger der Überweisung.
     *
     * @param receiver neuer Empfängername oder -kennung
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Berechnet den Betrag dieser Überweisung.
     * <p>
     * Da bei Transfers keine Zinsen anfallen, wird der Betrag unverändert zurückgegeben.
     *
     * @return unveränderter Betrag der Transaktion
     */
    @Override
    public double calculate() {
        return amount;
    }

    /**
     * Gibt eine lesbare Textdarstellung der Überweisung zurück.
     *
     * @return String mit allen Attributen und berechnetem Betrag
     */

    @Override
    public String toString() {
        return super.toString()
                + "\nSender: " + this.sender
                + "\nRecipient: " + this.receiver;
    }

    /**
     * Vergleicht zwei Objekte auf Gleichheit.
     * Zwei Transfers gelten als gleich, wenn alle Attribute – inklusive Sender
     * und Empfänger – identisch sind.
     *
     * @param o das zu vergleichende Objekt
     * @return {@code true}, wenn alle Werte gleich sind, sonst {@code false}
     */
    @Override
    public boolean equals(Object o) {

        if (!super.equals(o)) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(sender, transfer.sender)
                && Objects.equals(receiver, transfer.receiver);
    }
    public Transfer(Transfer t) {
        super(t);
        this.sender = t.sender;
        this.receiver = t.receiver;
    }

}
