package bank;
/**
 * Dieses Interface definiert eine Methode zur Berechnung des tatsächlichen
 * Betrags nach Anwendung von Gebühren, Zinsen oder anderen Faktoren.
 * Es wird von Transaktionsklassen implementiert, die unterschiedliche
 * Berechnungslogiken haben können.
 *{@link Payment} - Berechnet Betrag mit Ein-/Auszahlungszinsen
 {@link Transfer} - Gibt Betrag unverändert zurück
 */
public interface CalculateBill {
    public double calculate();
}
