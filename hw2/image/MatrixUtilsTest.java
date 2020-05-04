package image;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */
/** Non-destructively accumulates an energy matrix in the vertical
 *  direction.
 *
 *  Given an energy matrix M, returns a new matrix am[][] such that
 *  for each index i, j, the value in am[i][j] is the minimum total
 *  energy required to reach position i, j from any spot in the top
 *  row. See the bottom of this comment for an example.
 *
 *  Potentially useful methods: MatrixUtils.copy
 *
 *  A helper method you might consider writing:
 *    get(double[][] e, int r, int c): Returns the e[r][c] if
 *         r and c are valid. Double.POSITIVE_INFINITY otherwise
 *
 *  An example is shown below. See the assignment spec for a
 *  detailed explanation of this example.
 *
 *  Sample input:
 *  1000000   1000000   1000000   1000000
 *  1000000     75990     30003   1000000
 *  1000000     30002    103046   1000000
 *  1000000     29515     38273   1000000
 *  1000000     73403     35399   1000000
 *  1000000   1000000   1000000   1000000
 *
 *  Output for sample input:
 *  1000000   1000000   1000000   1000000
 *  2000000   1075990   1030003   2000000
 *  2075990   1060005   1133049   2030003
 *  2060005   1089520   1098278   2133049
 *  2089520   1162923   1124919   2098278
 *  2162923   2124919   2124919   2124919
 *
 */
public class MatrixUtilsTest {
    /** FIXME
     */


    @Test
    public void matrixutilsTest(){
        double[][] test = {
                {1000000,1000000,1000000,1000000},
                {1000000,75990,30003,1000000},
                {1000000,30002,103046,1000000},
                {1000000,29515,38273,1000000},
                {1000000,73403,35399,1000000},
                {1000000,1000000,1000000,1000000}
        };

        System.out.println(Arrays.deepToString(MatrixUtils.accumulateVertical(test)));
        System.out.println(Arrays.deepToString(MatrixUtils.accumulate(test, MatrixUtils.Orientation.HORIZONTAL)));
    }
    public static void main(String[] args) {

        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
