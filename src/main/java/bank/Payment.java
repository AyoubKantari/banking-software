package bank;
/**
 * Repräsentiert eine Bankzahlung mit eingehenden und ausgehenden Zinsen.
 * <p>
 * Bei Einzahlungen wird der eingehende Zins abgezogen,
 * bei Auszahlungen der ausgehende Zins addiert.
 */
public class Payment extends Transaction {
    private double incomingInterest;
    private double outgoingInterest;
    /**
     * Erstellt ein Payment-Objekt.
     *
     * @param amount            Betrag der Transaktion
     * @param date              Datum der Transaktion
     * @param description       Beschreibung der Transaktion
     * @param incomingInterest  Zinssatz für Einzahlungen (0–1)
     * @param outgoingInterest  Zinssatz für Auszahlungen (0–1)
     */
    public Payment(double amount,String date, String description, double incomingInterest, double outgoingInterest) {
            super(amount, date, description);
            this.incomingInterest = incomingInterest;
            this.outgoingInterest = outgoingInterest;
    }
    public double getIncomingInterest() {
        return incomingInterest;
    }
    public void setIncomingInterest(double incomingInterest) {
        if (incomingInterest < 0.0 || incomingInterest > 1.0) {
            System.out.println("Invalid incoming interest");

        }
        else {this.incomingInterest = incomingInterest;}
    }
    public void setOutgoingInterest(double outgoingInterest) {
        if (outgoingInterest < 0.0 || outgoingInterest > 1.0) {
            System.out.println("Invalid outgoing interest");
        } else {
            this.outgoingInterest = outgoingInterest;
        }
    }
    public double getOutgoingInterest() {
        return outgoingInterest;
    }
    /**
     * Berechnet den Endbetrag nach Zinsen.
     *
     * @return berechneter Betrag (mit Zinsen)
     */
    @Override
    public double calculate() {
        if (amount >= 0.0) {

            return amount - (amount * incomingInterest);
        } else {

            return amount * (1.0 + outgoingInterest);
        }
    }
    /** @return Textdarstellung inklusive berechnetem Betrag */

    @Override
    public String toString() {
        return super.toString()
                + "\nincomingInterest: " + incomingInterest
                + "\noutgoingInterest: " + outgoingInterest;
    }
    /** Vergleicht zwei Transaktionen auf Gleichheit. */

    @Override
    public boolean equals(Object o) {

        if (!super.equals(o)) return false;
        Payment payment = (Payment) o;
        return Double.compare(payment.incomingInterest, incomingInterest) == 0 &&
                Double.compare(payment.outgoingInterest, outgoingInterest) == 0;
    }
    public Payment(Payment p){
        super(p);
        this.incomingInterest = p.getIncomingInterest();
        this.outgoingInterest = p.getOutgoingInterest();
    }

}
