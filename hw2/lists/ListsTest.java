package lists;

import image.In;
import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {
    /** FIXME
     */
    @Test
    public  void listTest(){
        IntList b = IntList.list( 0, 1, 2, 3, 4 );
//        System.out.println(Lists.naturalRuns(a));
        System.out.println(Lists.naturalRuns(b));
    }
    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.
    public static void main(String[] args) {


        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
