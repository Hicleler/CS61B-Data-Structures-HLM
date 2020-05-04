import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Laiming Huang
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    public Reader rd;
    public String from;
    public String to;

    public TrReader(Reader str, String from, String to) {
        // TODO: YOUR CODE HERE
        this.rd = str;
        this.from = from;
        this.to = to;
    }

    /* TODO: IMPLEMENT ANY MISSING ABSTRACT METHODS HERE
     * NOTE: Until you fill in the necessary methods, the compiler will
     *       reject this file, saying that you must declare TrReader
     *       abstract. Don't do that; define the right methods instead!
     */

    @Override
    public int read(char[] str, int off, int len) throws IOException{
        int buffer = this.rd.read(str, off, len);
        if(buffer == -1){
            return -1;
        } else {
            for(int i = off; i< off + len; i ++){
                char curr = str[i];
                if(from.indexOf(curr) != -1){
                    int replacement = this.from.indexOf(str[i]);
                    str[i] = this.to.charAt(replacement);
                }
            }
        }
        return buffer;
    }

    @Override
    public void close() throws IOException{
        this.rd.close();
    }

}
