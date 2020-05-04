package enigma;

/**
 * Class that represents a rotating rotor in the enigma machine.
 *
 * @author Laiming Huang
 */
class MovingRotor extends Rotor {
    /**
     * The string representing the notches.
     */
    private String _notches;

    /**
     * A rotor named NAME whose permutation in its default setting is
     * PERM, and whose notches are at the positions indicated in NOTCHES.
     * The Rotor is initially in its 0 setting (first character of its
     * alphabet).
     */
    /**
     * @param name    name of rotor
     * @param perm    Permutation
     * @param notches Notches
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean reflecting() {
        return false;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < this._notches.length(); i++) {
            if (this.alphabet().toInt(this._notches.charAt(i))
                == this.setting()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return my notches
     */
    String myNotches() {
        return this._notches;
    }

    @Override
    void advance() {
        this.set(this.setting() + 1);
    }
}
