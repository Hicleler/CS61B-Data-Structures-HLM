import static org.junit.Assert.*;
import org.junit.Test;

public class IntListTest {

    /** Sample test that verifies correctness of the IntList.list static
     *  method. The main point of this is to convince you that
     *  assertEquals knows how to handle IntLists just fine.
     */

    @Test
    public void testList() {
        IntList one = new IntList(1, null);
        IntList twoOne = new IntList(2, one);
        IntList threeTwoOne = new IntList(3, twoOne);

        IntList x = IntList.list(3, 2, 1);
        assertEquals(threeTwoOne, x);
    }

    /** Do not use the new keyword in your tests. You can create
     *  lists using the handy IntList.list method.
     *
     *  Make sure to include test cases involving lists of various sizes
     *  on both sides of the operation. That includes the empty list, which
     *  can be instantiated, for example, with
     *  IntList empty = IntList.list().
     *
     *  Keep in mind that dcatenate(A, B) is NOT required to leave A untouched.
     *  Anything can happen to A.
     */

    @Test
    public void testDcatenate() {
        IntList t1 = IntList.list(1,2,3,4,5,6,7);
        IntList t2 = IntList.list(8,9,10,11);
        IntList twoone = IntList.list(2,1);
        IntList threeTwoOne = IntList.list(3, 2,1);
        IntList a = IntList.list(1);
        IntList b = IntList.list(2);
        assertEquals(IntList.list(1,2),IntList.dcatenate(a,b));
        assertEquals(IntList.dcatenate(IntList.list(),IntList.list(2,1)), twoone);
        assertEquals(IntList.dcatenate(IntList.list(),IntList.list(3,2,1)), threeTwoOne);
        assertEquals(IntList.dcatenate(IntList.list(3,2,1),IntList.list()), threeTwoOne);
        assertEquals(IntList.dcatenate(IntList.list(),IntList.list()), IntList.list());
        assertEquals(IntList.dcatenate(t1, t2), IntList.list(1,2,3,4,5,6,7,8,9,10,11));
    }

    /** Tests that subtail works properly. Again, don't use new.
     *
     *  Make sure to test that subtail does not modify the list.
     */

    @Test
    public void testSubtail() {
        IntList one = IntList.list(1,2,3,4,5,6,7);
        assertEquals(IntList.list(3,4,5,6,7), IntList.subTail(one, 2));
        assertEquals(IntList.list(4,5,6,7), IntList.subTail(one, 3));
        assertEquals(one, IntList.list(1,2,3,4,5,6,7));
    }

    /** Tests that sublist works properly. Again, don't use new.
     *
     *  Make sure to test that sublist does not modify the list.
     */

    @Test
    public void testSublist() {
        IntList one = IntList.list(1,2,3,4,5,6,7);
        assertEquals(IntList.list(3,4,5), IntList.sublist(one, 2,3));
        assertEquals(IntList.list(4,5,6,7), IntList.sublist(one, 3,4));
        assertEquals(one, IntList.list(1,2,3,4,5,6,7));
        assertEquals(null, IntList.dsublist(IntList.list(),0,0));

    }

    /** Tests that dSublist works properly. Again, don't use new.
     *
     *  As with testDcatenate, it is not safe to assume that list passed
     *  to dSublist is the same after any call to dSublist
     */

    @Test
    public void testDsublist() {
        IntList one = IntList.list(1,2,3,4,5,6,7);
        IntList one1 = IntList.list(1,2,3,4,5,6,7);
        IntList one2 = IntList.list(1,2,3,4,5);
        assertEquals(null, IntList.dsublist(one2,2,0));
        assertEquals(IntList.list(3,4,5), IntList.dsublist(one, 2,3));
        assertEquals(IntList.list(4,5,6,7), IntList.dsublist(one1, 3,4));
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(IntListTest.class));
    }
}
