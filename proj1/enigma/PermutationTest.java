package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static enigma.TestUtils.*;
import static org.junit.Assert.*;

/**
 * The suite of all JUnit tests for the Permutation class.
 *
 * @author
 */
public class PermutationTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checksplitCycles() {
        String cycles = "(ANOUPFRIMBZTLWKSVEGCJYDHXQ)";
        String[] expected = {"ANOUPFRIMBZTLWKSVEGCJYDHXQ"};
        assertArrayEquals(Permutation.cycleSplitter(cycles), expected);
    }

    @Test
    public void testInvertChar() {
        Alphabet testAlphabet = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
            testAlphabet);
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('A'), 'W');
        assertEquals(p.invert('P'), 'H');
    }

    @Test
    public void testPermuteChar() {
        Alphabet testAlphabet = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
            testAlphabet);
        assertEquals(p.permute('P'), 'N');
        assertEquals(p.permute('N'), 'H');
        assertEquals(p.permute('H'), 'P');
        assertEquals(p.permute('A'), 'B');
        assertEquals(p.permute('W'), 'A');
    }

    @Test
    public void testDerangement() {
        Alphabet testAlphabet = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
            testAlphabet);
        Permutation q = new Permutation("(PNM) (ABDFIKLZYXW) (J)",
            testAlphabet);
        Permutation e = new Permutation("(PNN) (ABDFIKLZYXW) (JC)",
            testAlphabet);
        assertTrue(p.derangement());
        assertFalse(q.derangement());
        assertFalse(e.derangement());

    }
}
