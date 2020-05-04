package enigma;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Represents a permutation of a range of integers starting at 0 corresponding
 * to the characters of an alphabet.
 *
 * @author Laiming Huang
 */
class Permutation {

    /**
     * Alphabet of this permutation.
     */
    private Alphabet _alphabet;
    /**
     * String of this cycle.
     */
    private String _cycles;

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /**
     * @param rawCycle The input cycle.
     * @return Splitted cycle
     */
    public static String[] cycleSplitter(String rawCycle) {
        String tempCycle = rawCycle;
        if (rawCycle.equals("")) {
            return null;
        }
        if (rawCycle.contains(" ")) {
            tempCycle = tempCycle.replaceAll(" ", "");
        }
        String[] tt = tempCycle.split("[(),]+");
        String[] slice = Arrays.copyOfRange(tt, 1, tt.length);
        Arrays.parallelSetAll(slice, (i) -> slice[i].trim());
        return slice;
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        _cycles += cycle;
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    int permute(int p) {
        int modP = wrap(p);
        char toC = _alphabet.toChar(modP);
        char cOut = permute(toC);
        int outP = _alphabet.toInt(cOut);
        return outP;
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int modC = wrap(c);
        char toC = _alphabet.toChar(modC);
        char cOut = invert(toC);
        int outP = _alphabet.toInt(cOut);
        return outP;
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        char output = p;
        String[] processedCycle = cycleSplitter(_cycles);
        if (_cycles.equals("") || processedCycle == null) {
            return output;
        }
        for (String piece : processedCycle) {
            if (piece.contains(Character.toString(p))) {
                int indexOfP = piece.indexOf(Character.toString(p));
                if (indexOfP != piece.length() - 1) {
                    output = piece.charAt(indexOfP + 1);
                } else {
                    output = piece.charAt(0);
                }
            }
        }
        return output;
    }

    /**
     * Return the result of applying the inverse of this permutation to C.
     */
    char invert(char c) {
        char output = c;
        String[] processedCycle = cycleSplitter(_cycles);
        if (_cycles.equals("") || processedCycle == null) {
            return output;
        }
        for (String piece : processedCycle) {
            if (piece.contains(Character.toString(c))) {
                int indexOfC = piece.indexOf(Character.toString(c));
                if (indexOfC != 0) {
                    output = piece.charAt(indexOfC - 1);
                } else {
                    output = piece.charAt(piece.length() - 1);
                }
            }
        }
        return output;
    }

    /**
     * Return the alphabet used to initialize this Permutation.
     */
    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Return true iff this permutation is a derangement (i.e., a
     * permutation for which no value maps to itself).
     */
    boolean derangement() {
        String tempCycle = _cycles;
        String[] processedTempCycle = cycleSplitter(tempCycle);
        boolean singletonCheck = false;
        boolean duplicateCheck = false;
        for (String piece : processedTempCycle) {
            if (piece.length() == 1) {
                singletonCheck = true;
            }
        }
        tempCycle = tempCycle.replaceAll(" ", "");
        tempCycle = tempCycle.replaceAll("[()]", "");
        Map<Character, Integer> map = new HashMap<Character, Integer>();
        for (int i = 0; i < tempCycle.length(); i++) {
            char c = tempCycle.charAt(i);
            if (map.containsKey(c)) {
                duplicateCheck = true;
                break;
            } else {
                map.put(c, 1);
            }
        }
        return !singletonCheck && !duplicateCheck;
    }

    /**
     * Check if there exists repeated character in the permutation.
     */
    void multipleCharTest() {
        HashSet<Character> set = new HashSet<Character>();
        String[] processedCycle = cycleSplitter(_cycles);
        for (String cycle: processedCycle) {
            for (int i = 0; i < cycle.length(); i++) {
                if (!set.add(cycle.charAt(i))) {
                    throw new
                        EnigmaException
                    ("Repeated character in the permutation!");
                }
            }
        }
    }
}


