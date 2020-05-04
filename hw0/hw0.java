public class hw0 {
    public static void main(String[] args) {
        int[] a = { 1, 2, 3, 4 };
        int[] b = { 8, 2,-1,-1,15};
        System.out.println(max(a));
        System.out.println(threeSum(b));
        System.out.println(threeSumDistinct(b));
    }

    public static int max(int[] a) {
        int temp = a[0];
        for (int i = 0; i < a.length; i++) {
            if (a[i] > temp) {
                temp = a[i];
            }
        }
        return temp;
    }

    public static boolean threeSum(int[] b){

        for(int i = 0; i < b.length; i++){
            int target1 = 0 - b[i];
            for(int j = 0; j < b.length; j++){
                    int target2 = target1 - b[j];
                    for(int k =0; k < b.length; k++){
                            if (b[k] == target2){
                                return true;
                            }
                    }
                
            }
        }
        return false;
    }

    public static boolean threeSumDistinct(int[] b){

        for(int i = 0; i < b.length; i++){
            int target1 = 0 - b[i];
            for(int j = 0; j < b.length; j++){
                    if(j!=i){
                    int target2 = target1 - b[j];
                    for(int k =0; k < b.length; k++){
                        if(k != j && k != i){
                            if (b[k] == target2){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}