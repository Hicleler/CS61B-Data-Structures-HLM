package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


import static enigma.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The suite of all JUnit tests for the Permutation class.
 *
 * @author
 */
public class AlphabetTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);


    /* ***** TESTS ***** */

    @Test
    public void alphabetTest1() {
        Alphabet testA = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        assertEquals(26, testA.size());
        assertEquals('C', testA.toChar(2));
        assertEquals(3, testA.toInt('D'));
        assertTrue(testA.contains('V'));
    }

    @Test(expected = EnigmaException.class)
    public void alphabetTest2() {
        Alphabet testA = new Alphabet("A(BCDEFG*HIJKL)MNOPQRSTUVWXYZ");
    }


}
