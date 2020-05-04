package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static enigma.EnigmaException.error;

/**
 * Enigma simulator.
 *
 * @author Laiming Huang
 */
public final class Main {

    /**
     * Alphabet used in this machine.
     */
    private Alphabet _alphabet;
    /**
     * Source of input messages.
     */
    private Scanner _input;
    /**
     * Source of machine configuration.
     */
    private Scanner _config;
    /**
     * File for encoded/decoded messages.
     */
    private PrintStream _output;
    /**
     * Number of rotor slots.
     */
    private int _numRotors;
    /**
     * Number of pawls.
     */
    private int _pawls;
    /**
     * Collection of available rotors.
     */
    private ArrayList<Rotor> _allRotors = new ArrayList<Rotor>();

    /**
     * Check ARGS and open the necessary files (see comment on main).
     */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */
    private void process() {
        try {
            String setting;
            Machine newMachine = readConfig();
            if (_input.hasNext("[*]")) {
                _input.next();
                setting = _input.nextLine();
                setUp(newMachine, setting);
            }
            while (_input.hasNextLine()) {
                String buffer = _input.nextLine();
                if (buffer.isEmpty()) {
                    _output.println(buffer);
                } else if (buffer.contains(Character.toString('*'))) {
                    setUp(newMachine, buffer);
                } else {
                    printMessageLine(newMachine.convert(buffer));
                }
            }
        } catch (NullPointerException excp) {
            throw new EnigmaException("No setting found!");
        }
    }

    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String temp = _config.next();
            _alphabet = new Alphabet(temp);
            _numRotors = _config.nextInt();
            _pawls = _config.nextInt();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, _numRotors, _pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String tempName = _config.next();
            String buffer = _config.next();
            String type = Character.toString(buffer.charAt(0));
            String tempCycle = "";
            while (_config.hasNext("\\s*\\(.+\\)\\s*")) {
                String temp = _config.next();
                tempCycle += temp;
            }
            Permutation tempPerm = new Permutation(tempCycle, _alphabet);
            tempPerm.multipleCharTest();
            if (type.equals("M")) {
                return new MovingRotor(tempName, tempPerm, buffer.substring(1));
            } else if (type.equals("N")) {
                return new FixedRotor(tempName, tempPerm);
            } else if (type.equals("R")) {
                return new Reflector(tempName, tempPerm);
            } else {
                throw new EnigmaException("Invalid rotor type!");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Determine if duplicate exists on a string array.
     *
     * @param string input string
     * @return boolean value
     */
    boolean duplicates(String[] string) {
        Set<String> set = new HashSet<String>();
        for (String i : string) {
            if (set.contains(i)) {
                return true;
            }
            set.add(i);
        }
        return false;
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        try {
            Pattern pattern = Pattern.compile("\\s*\\(.+\\)\\s*");
            Scanner settingsReader = new Scanner(settings);
            String[] allrotors = new String[_numRotors];
            if (settingsReader.hasNext("[*]")) {
                settingsReader.next();
            }
            for (int i = 0; i < _numRotors; i++) {
                allrotors[i] = settingsReader.next();
            }
            if (duplicates(allrotors)) {
                throw new EnigmaException("Repeated rotors!");
            }
            M.insertRotors(allrotors);
            String setting = settingsReader.next();
            M.setRotors(setting);
            String pbSetting = "";
            if (settingsReader.hasNext()) {
                String buffer = settingsReader.next();
                Matcher matcher = pattern.matcher(buffer);
                if (matcher.find()) {
                    pbSetting += buffer;
                } else {
                    M.setRotors(setting, buffer);
                }
                while (settingsReader.hasNext("\\s*\\(.+\\)\\s*")) {
                    pbSetting += settingsReader.next();
                }
                if (settingsReader.hasNext()) {
                    throw new EnigmaException
                    ("The setting line contains wrong number of arguments!");
                }
                Permutation plugboard = new Permutation(pbSetting, _alphabet);
                if (!(pbSetting .equals(""))) {
                    plugboard.multipleCharTest();
                }
                M.setPlugboard(plugboard);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad settings");
        }
    }

    /**
     * Print MSG in groups of five (except that the last group may
     * have fewer letters).
     */
    private void printMessageLine(String msg) {
        int count = 0;
        char[] msgChars = msg.toCharArray();
        for (int i = 0; i < msgChars.length; i++) {
            if (count % 5 == 0 && count != 0) {
                _output.print(" ");
            }
            _output.print(msgChars[i]);
            count++;
        }
        _output.println();
    }
}
