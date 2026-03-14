package bank;

/**
 * Repräsentiert eine ausgehende Überweisung von einem Konto.
 * <p>
 * Diese Klasse erbt von {@link Transfer} und kennzeichnet eine Überweisung,
 * bei der das aktuelle Konto der Sender ist. Der Betrag wird negativ
 * zum Kontostand verrechnet, wodurch das Guthaben reduziert wird.
 * </p>
 */
public class OutgoingTransfer extends Transfer {


    /**
     * Erstellt einen neuen ausgehenden Transfer mit Sender und Empfänger.
     *
     * @param date        Datum des Transfers (nicht null)
     * @param amount      positiver Betrag (wird intern negativ verrechnet)
     * @param description Beschreibung des Transfers
     * @param sender      Name des Senders (dieses Konto)
     * @param recipient   Name des Empfängers (externes Konto)
     */

    public OutgoingTransfer( double amount,String date, String description,
                            String sender, String recipient) {
        super( amount,date, description, sender, recipient);
    }

    /**
     * Berechnet den Wert des Transfers für das Konto.
     * @return negativer Betrag des Transfers
     */
    @Override
    public double calculate() {
        // Ausgehend: negativer Beitrag zum Kontostand
        return -super.calculate();
    }
}
