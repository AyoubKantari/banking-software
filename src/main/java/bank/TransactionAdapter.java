package bank;

import com.google.gson.*;

import java.lang.reflect.Type;
/**
 * Custom Gson-Adapter für die Serialisierung und Deserialisierung von TransactionObjekten
 * Diese Klasse implementiert sowohl {@link JsonSerializer} als auch {@link JsonDeserializer}
 *
 * Das  CLASSNAME-Feld speichert den einfachen Klassennamen und
 * ermöglicht beim Deserialisieren die Bestimmung der konkreten Subklasse.
 * Das  INSTANCEFeld enthält alle Attribute der Transaktion.

 */
public class TransactionAdapter implements JsonSerializer<Transaction> , JsonDeserializer<Transaction>{
    @Override
    /**
     * Serialisiert ein  Transaction-Objekt zu JSON.
     * @param tran    das zu serialisierende  Transaction Objekt
     * @param type    der Typ des zu serialisierenden Objekts (wird hier nicht verwendet)
     * @param context Gson-Kontext für verschachtelte Serialisierung (wird hier nicht verwendet)
     *
     * @return ein  JsonElement mit der verschachtelten JSON-Struktur bestehend
     *         aus äußerem Objekt mit CLASSNAME und innerem INSTANCE-Objekt mit allen Feldern
     *
     */
    public JsonElement serialize(Transaction tran, Type type,
                                 JsonSerializationContext context) {
        JsonObject ausser = new JsonObject();
        JsonObject instance = new JsonObject();


        String className = tran.getClass().getSimpleName();
        ausser.addProperty("CLASSNAME", className);


        instance.addProperty("amount", tran.getAmount());
        instance.addProperty("date", tran.getDate());
        instance.addProperty("description", tran.getDescription());

        if (tran instanceof Payment) {
            Payment p = (Payment) tran;
            instance.addProperty("incomingInterest", p.getIncomingInterest());
            instance.addProperty("outgoingInterest", p.getOutgoingInterest());
        } else if (tran instanceof IncomingTransfer) {
            Transfer t = (Transfer) tran;
            instance.addProperty("sender", t.getSender());
            instance.addProperty("recipient", t.getReceiver());
        } else if (tran instanceof OutgoingTransfer) {
            Transfer t = (Transfer) tran;
            instance.addProperty("sender", t.getSender());
            instance.addProperty("recipient", t.getReceiver());
        }

        ausser.add("INSTANCE", instance);
        return ausser;
    }
    /**
     * Deserialisiert JSON zu einem {@link Transaction}-Objekt
     * Diese Methode konvertiert eine JSON-Struktur mit {@code CLASSNAME} und
     * {@code INSTANCE} Feldern zurück in das entsprechende Java-{@code Transaction}-Objekt.

     * Wenn das {@code CLASSNAME}-Feld einen unbekannten Wert enthält (z.B. "UnknownTransaction"),
     * wird eine {@link JsonParseException} geworfen. Dies deutet auf eine beschädigte JSON-Datei
     * oder eine nicht unterstützte Transaktionsklasse hin. Die Exception enthält den unbekannten
     * Klassennamen in der Fehlermeldung zur besseren Fehleranalyse.

     * @param element das zu deserialisierende  JsonElement, muss ein JsonObject mit
     *                CLASSNAME und INSTANCE Feldern sein
     * @param context Gson-Kontext für verschachtelte Deserialisierung (wird hier nicht verwendet,
     *                da wir alle Felder manuell auslesen)
     *
     * @return ein neues  Transaction Objekt der entsprechenden Subklasse
     */

    @Override
    public Transaction deserialize(JsonElement element, Type type,
                                   JsonDeserializationContext context) {
        JsonObject ausser = element.getAsJsonObject();

        String className = ausser.get("CLASSNAME").getAsString();
        JsonObject instance = ausser.getAsJsonObject("INSTANCE");

        double amount = instance.get("amount").getAsDouble();
        String date = instance.get("date").getAsString();
        String description = instance.get("description").getAsString();

        if (className.equals("Payment")) {
            double in = instance.get("incomingInterest").getAsDouble();
            double out = instance.get("outgoingInterest").getAsDouble();
            return new Payment(amount, date, description, in, out);
        }
        else if (className.equals("IncomingTransfer")) {
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();
            return new IncomingTransfer(amount, date, description,
                    sender, recipient);
        }
        else if (className.equals("OutgoingTransfer")) {
            String sender = instance.get("sender").getAsString();
            String recipient = instance.get("recipient").getAsString();
            return new OutgoingTransfer(amount, date, description,
                    sender, recipient);
        }

        throw new JsonParseException("Unknown type: " + className);
    }
}