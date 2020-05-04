package enigma;

import static enigma.EnigmaException.error;

/**
 * Class that represents a reflector in the enigma.
 *
 * @author Laiming Huang
 */
class Reflector extends FixedRotor {

    /**
     * A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM.
     */
    Reflector(String name, Permutation perm) {
        super(name, perm);
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    boolean rotates() {
        return false;
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector can have only one position");
        }
        super.set(posn);
    }

}
