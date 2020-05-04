package enigma;

/**
 * Superclass that represents a rotor in the enigma machine.
 *
 * @author Laiming Huang
 */
class Rotor {

    /**
     * My name.
     */
    private final String _name;
    /**
     * The permutation implemented by this rotor in its 0 position.
     */
    private Permutation _permutation;
    /**
     * The integer representing the current setting.
     */
    private int _setting;
    /**
     * The integer representing the current ring.
     */
    private int _ring = 0;

    /**
     * A rotor named NAME whose permutation is given by PERM.
     */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _ring = 0;
    }

    /**
     * Return my name.
     */
    String name() {
        return _name;
    }

    /**
     * Return my alphabet.
     */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /**
     * Return my permutation.
     */
    Permutation permutation() {
        return _permutation;
    }

    /**
     * Return the size of my alphabet.
     */
    int size() {
        return _permutation.size();
    }

    /**
     * Return true iff I have a ratchet and can move.
     */
    boolean rotates() {
        return false;
    }

    /**
     * Return true iff I reflect.
     */
    boolean reflecting() {
        return false;
    }

    /**
     * Return my current setting.
     */
    int setting() {
        return _setting;
    }

    /**
     * Return my current ring setting.
     */
    int ring() {
        return _ring;
    }

    /**
     * Set setting() to POSN.
     */
    void set(int posn) {
        _setting = _permutation.wrap(posn);
    }

    /**
     * Set setting() to POSN.
     * @param ring ring number
     */
    void setRing(int ring) {
        _ring = _permutation.wrap(ring);
    }

    /**
     * Set setting() to character CPOSN.
     */
    void set(char cposn) {
        _setting = _permutation.wrap(_permutation.alphabet().toInt(cposn));
    }

    /**
     * Set setting() to character CPOSN.
     * @param cring ring char.
     */
    void setRing(char cring) {
        _ring = _permutation.wrap(_permutation.alphabet().toInt(cring));
    }

    /**
     * Return the conversion of P (an integer in the range 0..size()-1)
     * according to my permutation.
     */
    int convertForward(int p) {
        return _permutation.wrap
            (_permutation.permute
                (_permutation.wrap(p + _setting - _ring)) - (_setting - _ring));
    }

    /**
     * Return the conversion of E (an integer in the range 0..size()-1)
     * according to the inverse of my permutation.
     */
    int convertBackward(int e) {
        return _permutation.wrap
            (_permutation.invert
                (_permutation.wrap(e + _setting - _ring)) - (_setting - _ring));
    }

    /**
     * Returns true iff I am positioned to allow the rotor to my left
     * to advance.
     */
    boolean atNotch() {
        return false;
    }

    /**
     * Advance me one position, if possible. By default, does nothing.
     */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

}
