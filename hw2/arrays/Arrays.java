package arrays;
import com.sun.tools.corba.se.idl.Util;


/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] res = new int[A.length+B.length];
        for(int i=0;i<A.length;i++){
            res[i]=A[i];
        }
        for(int j=0;j<B.length;j++){
            res[A.length+j] = B[j];
        }
        return res;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int p = 0;
        int[] result = new int[A.length-len];
        while(p<start){
            result[p] = A[p];
            p += 1;
        }
        p += len;
        while(p<A.length){
            result[p-len] = A[p];
            p += 1;
        }
        return result;
    }


    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        if(A.length==0){
            return new int[0][];
        }
        if(A.length==1){
            return new int[][]{{A[0]}};
        }
        int slice = 0;
        int layer = 1;
        for(int i=1;i<A.length;i++){
            if(A[i-1]>=A[i]){
                layer += 1;
            }
        }
        int[][] out = new int[layer][];
        layer = 0;
        for(int i=1;i<A.length;i++){
            if(A[i-1]>=A[i]){
                out[layer] = Utils.subarray(A,slice,i-slice);
                layer += 1;
                slice = i;
            }
        }
        out[layer] = Utils.subarray(A, slice, A.length - slice);
        return out;
    }

}