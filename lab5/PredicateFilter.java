import java.util.Iterator;
import utils.Predicate;
import utils.Filter;

/** A kind of Filter that tests the elements of its input sequence of
 *  VALUES by applying a Predicate object to them.
 *  @author You
 */
class PredicateFilter<Value> extends Filter<Value> {

    /** A filter of values from INPUT that tests them with PRED,
     *  delivering only those for which PRED is true. */

    private Predicate<Value> thePred;

    PredicateFilter(Predicate<Value> pred, Iterator<Value> input) {
        super(input);
        thePred = pred;
    }

    @Override
    protected boolean keep() {
        return thePred.test(_next);
    }

    // FIXME: REPLACE THIS LINE WITH YOUR CODE

}
