package findReplaceString;

import java.util.Arrays;

class Solution {
    public String findReplaceString(String s, int[] indices, String[] sources, String[] targets) {
        int n = s.length() ;
        StringBuilder sb = new StringBuilder() ;

        int[] match = new int[n] ;
        Arrays.fill(match, -1) ;

        //crux of the solution
        //basically we are making a mapping here in match array at which point in original string, the current indices match
        // [ 0, 2 ] indices
        // [-1, -1, -1, -1] matches array before
        // [0, -1, -1, 1] matches array after

        for(int i = 0 ; i < indices.length; i++){
            if(indices[i] + sources[i].length() <= n &&
                    s.substring(indices[i], indices[i] + sources[i].length()).equals(sources[i])){
                match[indices[i]] = i ;
            }
        }

        int i = 0 ;

        while(i < n){
            if(match[i] != -1) {
                sb.append(targets[match[i]]) ;
                i+=sources[match[i]].length() ;
            }else {
                sb.append(s.charAt(i)) ;
                i++ ;
            }
        }

        return sb.toString() ;
    }
}
