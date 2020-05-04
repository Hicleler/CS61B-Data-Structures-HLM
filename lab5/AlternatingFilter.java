import java.util.Iterator;
import utils.Filter;

/** A kind of Filter that lets through every other VALUE element of
 *  its input sequence, starting with the first.
 *  @author Your Name
 */
class AlternatingFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that lets through every other
     *  value. */
    AlternatingFilter(Iterator<Value> input) {
        super(input); //FIXME?
        // FIXME: REPLACE THIS LINE WITH YOUR CODE
    }

    private boolean other = true;

    @Override
    protected boolean keep() {
        boolean pre = other;
        other = !other;
        return pre;
    }

    // FIXME: REPLACE THIS LINE WITH YOUR CODE

}