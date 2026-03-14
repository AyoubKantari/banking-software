package bank;

/**
 * Repräsentiert eine eingehende Überweisung auf ein Konto.
 * <p>
 * Diese Klasse erbt von {@link Transfer} und kennzeichnet eine Überweisung,
 * bei der das aktuelle Konto der Empfänger ist. Der Betrag wird positiv
 * zum Kontostand hinzugefügt (durch die geerbte calculate()-Methode).
 * </p>
 */
public class
IncomingTransfer extends Transfer{
    /**
     * Erstellt eine neue eingehende Überweisung.
     *
     * @param amount      Betrag der Überweisung (muss >= 0 sein)
     * @param date        Datum der Transaktion
     * @param description Beschreibung oder Verwendungszweck
     * @param sender      Name des Absenders (externes Konto)
     * @param receiver    Name des Empfängers (dieses Konto)
     */
    public IncomingTransfer(double amount, String date, String description,
                            String sender, String receiver) {
        super(amount, date, description, sender, receiver);
    }

    /**
     * Berechnet den Wert dieser Überweisung für das Konto.
     * <p>
     * Bei eingehenden Transfers wird der Betrag positiv verrechnet,
     * d.h. er erhöht den Kontostand. Diese Methode wird von der
     * Basisklasse {@link Transfer} geerbt.
     * </p>
     *
     * @return positiver Betrag der Überweisung
     */
    // Die calculate()-Methode wird von Transfer geerbt und gibt
    // den Betrag unverändert (positiv) zurück
}
