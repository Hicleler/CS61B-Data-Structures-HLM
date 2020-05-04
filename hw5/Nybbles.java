/** Represents an array of integers each in the range -8..7.
 *  Such integers may be represented in 4 bits (called nybbles).
 *  @author
 */
public class Nybbles {

    /** Maximum positive value of a Nybble. */
    public static final int MAX_VALUE = 7;

    /** Return an array of size N. */
    public Nybbles(int N) {
        // DON'T CHANGE THIS.
        _data = new int[(N + 7) / 8];
        _n = N;
    }

    /** Return the size of THIS. */
    public int size() {
        return _n;
    }

    /** Return the Kth integer in THIS array, numbering from 0.
     *  Assumes 0 <= K < N. */
    public int get(int k) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else {
            int i = k / 8;
            int j = k % 8;
            int e = _data[i] >>> j * 4;
            e &=  0b1111;
            if(e>=8) {
                return e - 16;
            } else {
                return e ;
            }
        }
    }

    /** Set the Kth integer in THIS array to VAL.  Assumes
     *  0 <= K < N and -8 <= VAL < 8. */
    public void set(int k, int val) {
        if (k < 0 || k >= _n) {
            throw new IndexOutOfBoundsException();
        } else if (val < (-MAX_VALUE - 1) || val > MAX_VALUE) {
            throw new IllegalArgumentException();
        } else {
            int i = k / 8;
            int j = k % 8;
            int temp = ~(0b1111 << j*4);
            int res = 0;
            res &= temp;
            int e = val;
            if(e<0) {
                e += 16;
            }
            int temp1 = val & 0b1111;
            temp1 <<= j*4;
            int ress = _data[i] |= temp1;
            _data[i] = ress;

        }
    }

    // DON'T CHANGE OR ADD TO THESE.
    /** Size of current array (in nybbles). */
    private int _n;
    /** The array data, packed 8 nybbles to an int. */
    private int[] _data;
}
