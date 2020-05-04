package enigma;


/**
 * An alphabet of encodable characters.  Provides a mapping from characters
 * to and from indices into the alphabet.
 *
 * @author Laiming Huang
 */
class Alphabet {
    /**
     * The array representing alphabet.
     */
    private char[] alphabet;

    /**
     * A new alphabet containing CHARS.  Character number #k has index
     * K (numbering from 0). No character may be duplicated.
     */

    Alphabet(String chars) {
        if (chars.contains("*") || chars.contains(" ")
            || chars.contains("(") || chars.contains(")")) {
            throw new EnigmaException("Invalid alphabet!");
        }
        alphabet = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            alphabet[i] = chars.charAt(i);
        }
    }


    /**
     * A default alphabet of all upper-case characters.
     */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /**
     * Returns the size of the alphabet.
     */
    int size() {
        return alphabet.length;
    }

    /**
     * Returns true if preprocess(CH) is in this alphabet.
     */
    boolean contains(char ch) {
        boolean found = false;
        for (int i = 0; i < size(); i++) {
            if (alphabet[i] == ch) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Returns character number INDEX in the alphabet, where
     * 0 <= INDEX < size().
     */
    char toChar(int index) {
        return alphabet[index];
    }

    /**
     * Returns the index of character preprocess(CH), which must be in
     * the alphabet. This is the inverse of toChar().
     */
    int toInt(char ch) {
        if (!contains(ch)) {
            throw new
                EnigmaException("Character " + ch + " not found in alphabet!");
        }
        for (int i = 0; i < size(); i++) {
            if (alphabet[i] == ch) {
                return i;
            }
        }
        return -1;
    }

}
