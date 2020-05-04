import java.util.List;
import java.util.LinkedList;

/** A set of String values.
 *  @author
 */
    class ECHashStringSet implements StringSet {
        private static double MIN = 0.2;
        private static double MAX = 5;

        private LinkedList<String>[] myList;
        private int mySize;

        public ECHashStringSet() {
            mySize = 0;
            myList = new LinkedList[(int)(1/MIN)];
        }

        @Override
        public void put(String s) {
            if(s!=null){
                if(myLoad()>MAX){
                    resize_array();
                }
                int loc = toLoc(s.hashCode());
                if(myList[loc]==null){
                    myList[loc] = new LinkedList<String>();
                }
                myList[loc].add(s);
                mySize++;
            }

        }

        @Override
        public boolean contains(String s) {
            if(s != null){
                int loc = toLoc(s.hashCode());

                if(myList[loc] == null)
                    return false;
                else
                    return myList[loc].contains(s);
            } else {
                return false;
            }
        }


        private void resize_array(){
            LinkedList<String>[] prev = myList;
            myList = new LinkedList[2*prev.length];
            mySize = 0;

            for(LinkedList<String> ll : prev) {
                if (ll != null) {
                    for (String l : ll) {
                        put(l);
                    }
                }
            }

        }

        private double myLoad(){
            return (double)(double)mySize /((double)myList.length);
        }

        private int toLoc(int hash){
            int toRm = hash & 1;
            int temp = (hash >>> 1) | toRm;

            return temp % myList.length;
        }

    @Override
    public List<String> asList() {
        LinkedList<String> out = new LinkedList<String>();
        for(LinkedList<String> ll : myList) {
            if (ll != null) {
                for (String l : ll) {
                    out.add(l);
                }
            }
        }
        return out;
    }
}
