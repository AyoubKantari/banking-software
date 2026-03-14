package bank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PaymentTest {
    private Payment payment1;
    private Payment payment2;
    @BeforeEach
    void setUp() {

        payment1 = new Payment(500.0, "2024-01-15", "Gehalt", 0.05, 0.03);
        payment2 = new Payment(-100.0, "2024-01-20", "Einkauf", 0.05, 0.03);
    }
    @Test
    void testConstructor() {

        String date = "2024-01-15";
        double amount = 500.0;
        String description = "Gehalt";
        double interest = 0.05;
        double out = 0.03;



        assertEquals(date, payment1.getDate());
        assertEquals(amount, payment1.getAmount());
        assertEquals(description, payment1.getDescription());
        assertEquals(interest,payment1.getIncomingInterest());
        assertEquals(out,payment1.getOutgoingInterest());
    }

    @Test
    void testcopy() {
        Payment copy = new Payment(payment1);
        assertEquals(payment1.getDate(), copy.getDate());
        assertEquals(payment1.getAmount(), copy.getAmount());
        assertEquals(payment1.getDescription(), copy.getDescription());
        assertEquals(payment1.getIncomingInterest(), copy.getIncomingInterest());
        assertEquals(payment1.getOutgoingInterest(), copy.getOutgoingInterest());
    }

    @Test
    void setIncomingInterestValid() {
        payment1.setIncomingInterest(0.08);  // Gültig!
        assertEquals(0.08, payment1.getIncomingInterest(), 0.001);
    }
    @Test
    void setOutgoingInterestValid() {
        payment1.setOutgoingInterest(0.08);  // ✓ Gültig!
        assertEquals(0.08, payment1.getOutgoingInterest(), 0.001);
    }
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 50.0, 100.0, 500.0, 1000.0})
    void testCalculatePositiveAmounts(double amount) {
        Payment p = new Payment(amount, "2024-01-15", "Test", 0.1, 0.2);
        double expected = amount - (amount * 0.1);
        assertEquals(expected, p.calculate(), 0.001);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -50.0, -100.0, -500.0})
    void testCalculateNegativeAmounts(double amount) {
        Payment p = new Payment(amount, "2024-01-15", "Test", 0.1, 0.2);
        double expected = amount * (1.0 + 0.2);
        assertEquals(expected, p.calculate(), 0.001);
    }
    @Test
    void equalstest(){
        Payment copy1 = new Payment(payment1);
        assertTrue(payment1.equals(copy1));
    }
    @Test
    void equalsfalseTest(){
        assertNotEquals(payment1, payment2);
        assertNotEquals(payment1, null);
        assertNotEquals(payment1, "hello world");
    }
    @Test

    void testToString(){
        String result = payment1.toString();


        assertTrue(result.contains("500.0"));
        assertTrue(result.contains("2024-01-15"));
        assertTrue(result.contains("Gehalt"));
    }
}
