package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class TransferTest {
    private Transfer transfer;
    @BeforeEach

    void setUp()
    {
       transfer = new Transfer(100, "22-10-2203","überweisung","ayoub", "aymen");
    }
    @Test
    void testconstructure(){
        double amount = 100;
        String date = "22-10-2203";
        String description = "überweisung";
        String sender = "ayoub";
        String receiver = "aymen";
        assertEquals(amount,transfer.getAmount());
        assertEquals(date,transfer.getDate());
        assertEquals(description,transfer.getDescription());
        assertEquals(sender,transfer.getSender());
        assertEquals(receiver,transfer.getReceiver());


    }
    @Test
    void testcopyconst(){
        Transfer t1 = new Transfer(transfer);
        assertEquals(transfer.getAmount(),t1.getAmount());
        assertEquals(transfer.getDate(),t1.getDate());
        assertEquals(transfer.getDescription(),t1.getDescription());
        assertEquals(transfer.getSender(),t1.getSender());
        assertEquals(transfer.getReceiver(),t1.getReceiver());

    }
    @Test
    void testequals (){
        Transfer t1 = new Transfer(transfer);
        assertEquals(transfer,t1);
        Transfer t2 = new Transfer(200, "2024-02-01", "anders", "other", "person");
        assertNotEquals(transfer, t2);

    }
    @Test
    void testTostring(){
       String result = transfer.toString();
       assertTrue(result.contains(transfer.getDate()));
       assertTrue(result.contains(transfer.getDescription()));
       assertTrue(result.contains(transfer.getSender()));
       assertTrue(result.contains(transfer.getReceiver()));

    }
    /**
     * Test calculate() für IncomingTransfer mit verschiedenen Beträgen.
     * IncomingTransfer gibt den Betrag POSITIV zurück (Geld kommt rein).
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.0, 100.0, 500.0, 1000.0, 5000.0})
    void testCalculateIncomingTransferWithDifferentAmounts(double amount) {
        // ARRANGE
        IncomingTransfer incoming = new IncomingTransfer(amount, "2024-01-15",
                "Test", "sender", "receiver");

        // ACT
        double result = incoming.calculate();

        // ASSERT
        // IncomingTransfer gibt Betrag POSITIV zurück
        assertEquals(amount, result, 0.001);
        assertTrue(result >= 0);  // Muss positiv oder 0 sein
    }

    /**
     * Test calculate() für OutgoingTransfer mit verschiedenen Beträgen.
     * OutgoingTransfer gibt den Betrag NEGATIV zurück (Geld geht raus).
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.0, 100.0, 500.0, 1000.0, 5000.0})
    void testCalculateOutgoingTransferWithDifferentAmounts(double amount) {
        // ARRANGE
        OutgoingTransfer outgoing = new OutgoingTransfer(amount, "2024-01-15",
                "Test", "sender", "receiver");

        // ACT
        double result = outgoing.calculate();

        // ASSERT
        // OutgoingTransfer gibt Betrag NEGATIV zurück
        assertEquals(-amount, result, 0.001);
        assertTrue(result <= 0);  // Muss negativ oder 0 sein
    }
    @Test
    void testCalculateOutgoingTransfer() {
        OutgoingTransfer outgoing = new OutgoingTransfer(100, "2024-01-15",
                "Test", "sender", "receiver");
        assertEquals(-100.0, outgoing.calculate());  // NEGATIV!
    }

}
