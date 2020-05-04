import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        /** Sample assert statement for comparing integers.

        assertEquals(0, 0); */
        assertEquals(1,CompoundInterest.numYears(2020));
        assertEquals(30,CompoundInterest.numYears(2049));
    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        assertEquals(12.544, CompoundInterest.futureValue(10, 12,2021), tolerance);
        assertEquals(16.2, CompoundInterest.futureValue(20,-10, 2021), tolerance);
        assertEquals(12.1, CompoundInterest.futureValue(10,10, CompoundInterest.THIS_YEAR+2), tolerance);

    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertEquals(11.8026496, CompoundInterest.futureValueReal(10, 12,2021, 3), tolerance);
        assertEquals(295712.28, CompoundInterest.futureValueReal(1000000, 0,2059, 3), tolerance);

    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertEquals(16550, CompoundInterest.totalSavings(5000, CompoundInterest.THIS_YEAR+2,10), tolerance);
        assertEquals(19860, CompoundInterest.totalSavings(6000, 2021,10), tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertEquals(15571.895, CompoundInterest.totalSavingsReal(5000, 2021,10,3), tolerance);
        assertEquals(18302.976, CompoundInterest.totalSavingsReal(6000, 2021,10,4), tolerance);

    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
