package WildCardMatching;

import java.util.Arrays;

public class Solution {
    public boolean isMatch(String s, String p) {
        int dp[][] = new int[s.length()][p.length()] ;
        for(int arr[]: dp)
            Arrays.fill(arr, -1);
        return helper(s, p, s.length() -1, p.length() - 1, dp) ;
    }

    //traversing from the last
    private boolean helper( String s, String p, int i, int j,  int[][] dp) {
        if(i< 0 && j < 0) return true ;
        if(i>=0 && j < 0) return false ;
        if(i<0 && j>=0){
            for(int k =0; k<=j ;k++){
                if(p.charAt(k) !='*') return  false ;
            }
            return true ;
        }

        if(dp[i][j] != -1) return dp[i][j] == 1 ;

        boolean result = false ;
        if(s.charAt(i) == p.charAt(j) || p.charAt(j) == '?') result = helper(s, p, i-1, j-1, dp) ;
        if(p.charAt(j) == '*') result = helper(s, p, i, j-1, dp) || helper(s, p, i-1, j, dp) ;

        dp[i][j] =  result ? 1 : 0 ;
        return result ;
    }

    public boolean match(String s, String p) {
        int n = s.length() ;
        int m = p.length() ;
        boolean dp[][] = new boolean[n+1][m+1] ;
        dp[0][0] = true ;
        for(int i=1; i < n+ 1; i++){
            dp[i][0]= false ;
        }
        for(int i = 1 ; i<m+1; i++){
            if(p.charAt(i-1) == '*'){
                dp[0][i] = dp[0][i-1];
            }
        }

        for(int i=1;i<n+1;i++){
            for(int j=1;j<m+1;j++){
                if(s.charAt(i-1)==p.charAt(j-1)|| p.charAt(j-1)=='?'){
                    dp[i][j]=dp[i-1][j-1];

                }else if(p.charAt(j-1)=='*'){
                    dp[i][j]=dp[i-1][j]||dp[i][j-1];
                }else{
                    dp[i][j]=false;
                }
            }
        }
        return dp[n][m] ;
    }
}
