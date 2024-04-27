package sudokuSolver;
import java.util.*;

//program to check whether a filled sudoku board is valid or not.
public class SudokuChecker {
    int[][] grid ;
    String ROW = "ROW" ;
    String COL = "COL" ;
    String BOX = "BOX" ;

    Map<String, List<HashSet<Integer>>> setsMap ;

    public SudokuChecker(int[][] grid){
        this.grid = grid ;
        this.setsMap = new HashMap<>() ;
        setsMap.put(ROW, new ArrayList<>()) ;
        setsMap.put(COL, new ArrayList<>()) ;
        setsMap.put(BOX, new ArrayList<>()) ;

        for(int i = 0; i< 9 ; i++){
            setsMap.get(ROW).add(new HashSet<>()) ;
            setsMap.get(COL).add(new HashSet<>()) ;
            setsMap.get(BOX).add(new HashSet<>()) ;
        }
    }
    public boolean isValid(){
        for(int i =0; i < 9; i++){
            for(int j = 0; j < 9 ;j++){
                int curDig = grid[i][j] ;
                int boxNumber = 3*(i/3) + (j/3) ;
                boolean res = setsMap.get(ROW).get(i).add(curDig)
                        && setsMap.get(COL).get(j).add(curDig)
                        && setsMap.get(BOX).get(boxNumber).add(curDig) ;
                if(!res || curDig == 0){
                    return false ;
                }
            }
        }
        return true ;
    }
}
