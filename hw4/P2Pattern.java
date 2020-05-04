/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static final String P1 = "^(((0?[1-9]|1[012])/(0?[1-9]|1\\d|2[0-8])|(0?[13456789]|1[012])/(29|30)|(0?[13578]|1[02])/31)/(19|[2-9]\\d)\\d{2}|0?2/29/((19|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|(([2468][048]|[3579][26])00)))$"; //FIXME: Add your regex here

    /** Pattern to match 61b notation for literal IntLists. */
    public static final String P2 = "\\(((0|[1-9]\\d*),(\\s*))*(0|[1-9]\\d*)\\)";

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static final String P3 = "^((xn--)?[a-z0-9]+(-[a-z0-9]+)*\\.)+[a-z]{2,}"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static final String P4 = "[a-zA-Z$_][a-zA-Z0-9$_]*"; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    public static final String P5 = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"; //FIXME: Add your regex here

}
