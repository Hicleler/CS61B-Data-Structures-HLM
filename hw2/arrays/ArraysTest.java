package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class ArraysTest {

    @Test
    public  void arraysTest(){
        int[] A = {1,2,3,4,5};
        int[] B = {6,7,8,9};
        int[] c = Arrays.catenate(A, B);
        int[] d = Arrays.remove(A,1,3);
        int[] e = {1, 3, 7, 5, 4, 6, 9, 10};
        int[][] ed = Arrays.naturalRuns(e);
        assertArrayEquals(c, new int[]{1,2,3,4,5,6,7,8,9});
        assertArrayEquals(d, new int[]{1,5});
        assertArrayEquals(ed,  new int[][]{{1, 3, 7}, {5}, {4, 6, 9, 10}});
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
