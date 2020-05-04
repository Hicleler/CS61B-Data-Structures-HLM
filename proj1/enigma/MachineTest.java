package enigma;
import org.junit.Test;

import java.util.ArrayList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Machine Tests.
 *
 * @author Laiming Huang
 */


public class MachineTest {
    Alphabet testAlphabet = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    MovingRotor A =
        new MovingRotor("I", new
            Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
            + "(DFG) (IV) (JZ) (S)", testAlphabet), "Q");
    MovingRotor B =
        new MovingRotor("II", new
            Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) "
            + "(BJ) (GR) (NT) (A) (Q)", testAlphabet), "E");
    MovingRotor C =
        new MovingRotor("III", new
            Permutation("(ABDHPEJT) "
            + "(CFLVMZOYQIRWUKXSG) (N)", testAlphabet), "V");
    MovingRotor D =
        new MovingRotor("IV", new
            Permutation("(AEPLIYWCOXMRFZBSTGJQNH) "
            + "(DV) (KU)", testAlphabet), "J");
    MovingRotor E =
        new MovingRotor("V", new
            Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) "
            + "(EGTJPX)", testAlphabet), "Z");
    MovingRotor F  =
        new MovingRotor("VI", new
            Permutation("(AJQDVLEOZWIYTS) (CGMNHFUX) "
            + "(BPRK)", testAlphabet), "ZM");
    MovingRotor G =
        new MovingRotor("VII", new
            Permutation("(ANOUPFRIMBZTLWKSVEGCJYDHXQ)", testAlphabet), "ZM");
    MovingRotor H =
        new MovingRotor("VIII",
            new Permutation("(AFLSETWUNDHOZVICQ) (BKJ)"
                + " (GXY) (MPR)", testAlphabet), "ZM");
    FixedRotor I =
        new FixedRotor("Beta", new Permutation("(ALBEVFCYODJW"
            + "UGNMQTZSKPR) (HIX)", testAlphabet));
    FixedRotor J =
        new FixedRotor("Gamma", new Permutation("(AFNIRLBSQWVXG"
            + "UZDKMTPCOYJHE)", testAlphabet));
    Reflector K  =
        new Reflector("B", new
            Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW)"
            + " (IJ) (LO) (MP) (RX) (SZ) (TV)", testAlphabet));
    Reflector L =
        new Reflector("C", new
            Permutation("AR) (BD) (CO) (EJ) (FN) (GT) (HK) "
            + "(IV) (LM) (PW) (QZ) (SX) (UY)", testAlphabet));
    private ArrayList<Rotor> _allrotors = new ArrayList<>();

    public void testInsertRotors() {
        _allrotors.add(A);
        _allrotors.add(B);
        _allrotors.add(C);
        _allrotors.add(D);
        _allrotors.add(E);
        _allrotors.add(F);
        _allrotors.add(G);
        _allrotors.add(H);
        _allrotors.add(I);
        _allrotors.add(J);
        _allrotors.add(K);
        _allrotors.add(L);
    }

    @Test
    public void numRotorsPawls() {
        testInsertRotors();
        Machine testMachine1 = new Machine(testAlphabet, 5, 3, _allrotors);
        assertEquals(testMachine1.numRotors(), 5);
        assertEquals(testMachine1.numPawls(), 3);
    }


    @Test
    public void insertRotorsRight() {
        testInsertRotors();
        Machine teshMachine2 = new Machine(testAlphabet, 5, 3, _allrotors);
        teshMachine2.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        Rotor[] rotorList = new Rotor[5];
        rotorList[0] = K;
        rotorList[1] = I;
        rotorList[2] = A;
        rotorList[3] = B;
        rotorList[4] = C;
        assertArrayEquals(teshMachine2.myRotors(), rotorList);
    }

    @Test
    public void setRotorsTest() {
        testInsertRotors();
        Machine testMachine3 = new Machine(testAlphabet, 5, 3, _allrotors);
        testMachine3.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        testMachine3.setRotors("AXLE");
        String input = "AEPLIYWCOXMRFZBSTGJQNH";
        String output = "QBTPSOIIRIJXRLNNQSACHQ";
        assertEquals(testMachine3.convert(input), output);
    }

    @Test
    public void setPlugboardTest() {
        testInsertRotors();
        Machine testMachine4 = new Machine(testAlphabet, 5, 3, _allrotors);
        testMachine4.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        testMachine4.setRotors("AXLE");
        testMachine4.setPlugboard(new Permutation("(TD) (KC) (JZ)",
            testAlphabet));
        String input = "QBTPSOIIRIJXRLNNQSACHQ";
        String output = "AEULIYWKOXTRFJBSDGZENH";
        assertEquals(testMachine4.convert(input), output);
    }

    @Test
    public void convertTest1() {
        testInsertRotors();
        Machine testMachine5 = new Machine(testAlphabet, 5, 3, _allrotors);
        testMachine5.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        testMachine5.setRotors("BBBB");
        String input = "VWDGEVURADWFYGLVFKJNDD";
        String output = "AEULIYWKOXTRFJBSDGZENH";
        assertEquals(testMachine5.convert(input), output);
    }

    @Test
    public void convertTest2() {
        testInsertRotors();
        Machine testMachine6 = new Machine(testAlphabet, 5, 3, _allrotors);
        testMachine6.insertRotors(new String[]{"B", "Beta", "I", "II", "III"});
        testMachine6.setRotors("BBBB");
        assertEquals(testMachine6.convert(0), 21);
    }


}
