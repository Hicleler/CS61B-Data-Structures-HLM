import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets all the VALUE elements of its input sequence
 *  that are larger than all the preceding values to go through the
 *  Filter.  So, if its input delivers (1, 2, 3, 3, 2, 1, 5), then it
 *  will produce (1, 2, 3, 5).
 *  @author You
 */
class MonotonicFilter<Value extends Comparable<Value>> extends Filter<Value> {

    /** A filter of values from INPUT that delivers a monotonic
     *  subsequence.  */
    MonotonicFilter(Iterator<Value> input) {
        super(input); //FIXME?
        // FIXME: REPLACE THIS LINE WITH YOUR CODE
    }

    private Value curr_max;

    @Override
    protected boolean keep() {
        if(curr_max == null || curr_max.compareTo(_next) < 0){
            curr_max = _next;
            return true;
        }
        return false;
    }
    
    // FIXME: ADD ANY ADDITIONAL FIELDS REQUIRED HERE

}
