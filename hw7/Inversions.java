import java.util.Arrays;
import java.util.List;

/** HW #7, Count inversions.
 *  @author
 */
public class Inversions {

    /** A main program for testing purposes.  Prints the number of inversions
     *  in the sequence ARGS. */
    public static void main(String[] args) {
        System.out.println(inversions(Arrays.asList(args)));
    }

    /** Return the number of inversions of T objects in ARGS. */
    public static <T extends Comparable<? super T>>
        int inversions(List<T> args) {
            int inv_count = 0;
            for (int i = 0; i < args.size() - 1; i++)
                for (int j = i + 1; j < args.size(); j++)
                    if (args.get(i).compareTo(args.get(j))<0)
                        inv_count++;

            return inv_count;

    }

}
