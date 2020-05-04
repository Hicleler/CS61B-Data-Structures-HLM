package enigma;

import java.util.Collection;

/**
 * Class that represents a complete enigma machine.
 *
 * @author Laiming Huang
 */
class Machine {

    /**
     * Common alphabet of my rotors.
     */
    private Alphabet _alphabet;
    /**
     * Number of rotors.
     */
    private int _numRotors;
    /**
     * Number of pawls.
     */
    private int _pawls;
    /**
     * Collection of all rotors.
     */
    private Collection<Rotor> _allRotors;
    /**
     * Array of my rotors.
     */
    private Rotor[] _myRotors;
    /**
     * My permutation.
     */
    private Permutation _myPlugBoard;

    /**
     * Return my rotors.
     * @return my rotors
     */
    Rotor[] myRotors() {
        return _myRotors;
    }

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _myRotors = new Rotor[_numRotors];
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return this._numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return this._pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        Rotor[] allTheRotors = _allRotors.toArray(new Rotor[_allRotors.size()]);
        _myRotors = new Rotor[_numRotors];
        for (int i = 0; i < numRotors(); i++) {
            for (int j = 0; j < _allRotors.size(); j++) {
                if (allTheRotors[j].name().equals(rotors[i])) {
                    _myRotors[i] = allTheRotors[j];
                }
            }
            if (_myRotors[i] == null) {
                throw new EnigmaException
                ("No available rotor for given setting!");
            }
        }

        int numMovingRotors = 0;
        for (int i = 0; i < numRotors(); i++) {
            if (_myRotors[i].rotates()) {
                numMovingRotors++;
            }
        }
        for (int i = 1; i < numRotors(); i++) {
            if (_myRotors[i].reflecting()) {
                throw new EnigmaException
                ("Rotors that are not the left most can not be reflector!");
            }
        }
        if (!_myRotors[0].reflecting()) {
            throw new EnigmaException
            ("The left-most rotor is not a reflector!");
        }
        if (!_myRotors[0].permutation().derangement()) {
            throw new EnigmaException
            ("The permutation for this reflector is not a derangement!");
        }
        if (numMovingRotors != numPawls()) {
            throw new EnigmaException
            ("Number of moving motors and number of pawls mismatch!");
        }

    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException
            ("Setting length and number of rotors does not match!");
        }
        for (int j = 0; j < setting.length(); j++) {
            if (!_alphabet.contains(setting.charAt(j))) {
                throw new EnigmaException
                ("Setting contain character(s) not in the alphabet!");
            }
        }
        for (int i = 0; i < setting.length(); i++) {
            _myRotors[i + 1].set(setting.charAt(i));
            _myRotors[i + 1].setRing(0);
        }
    }

    /**
     * Set my rotors according to SETTING with RING SETTING,
     * which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting, String ring) {
        if (setting.length() != ring.length()) {
            throw new EnigmaException
            ("Ring length and setting length does not match!");
        }
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException
            ("Setting length and number of rotors does not match!");
        }
        for (int j = 0; j < setting.length(); j++) {
            if (!_alphabet.contains(setting.charAt(j))) {
                throw new EnigmaException
                ("Setting contain character(s) not in the alphabet!");
            }
        }
        for (int i = 0; i < setting.length(); i++) {
            _myRotors[i + 1].set(setting.charAt(i));
            _myRotors[i + 1].setRing(ring.charAt(i));
        }
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _myPlugBoard = plugboard;
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % _alphabet.size();
        if (r < 0) {
            r += _alphabet.size();
        }
        return r;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * <p>
     * the machine.
     */
    int convert(int c) {
        c = wrap(c);
        if (this._myPlugBoard != null) {
            c = _myPlugBoard.permute(c);
        }
        for (int i = 1; i < _numRotors; i++) {
            if (i == _numRotors - 1
                || (_myRotors[i + 1].atNotch() && (_myRotors[i].rotates()))
                || (_myRotors[i].atNotch() && _myRotors[i - 1].rotates()
                && _myRotors[i].rotates())) {
                _myRotors[i].advance();
            }
        }
        for (int i = _numRotors - 1; i >= 0; i--) {
            c = _myRotors[i].convertForward(c);
        }
        for (int j = 1; j < _numRotors; j++) {
            c = _myRotors[j].convertBackward(c);
        }
        if (this._myPlugBoard != null) {
            c = _myPlugBoard.permute(c);
        }
        c = wrap(c);
        return c;
    }

    /**
     * Returns the encoding/decoding of MSG, updating the state of
     * the rotors accordingly.
     */
    String convert(String msg) {
        String res = "";
        for (int i = 0; i < msg.length(); i++) {
            if (_alphabet.contains(msg.charAt(i))) {
                res += Character.toString
                    (_alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i)))));
            } else {
                if (!Character.isWhitespace(msg.charAt(i))) {
                    throw new
                        EnigmaException("Character "
                        + msg.charAt(i) + " not found in alphabet!");
                }
            }
        }
        return res;
    }
}
