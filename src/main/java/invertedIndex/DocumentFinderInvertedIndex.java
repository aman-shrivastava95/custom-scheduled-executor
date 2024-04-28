package invertedIndex;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.Inet4Address;
import java.util.*;


@Getter
@AllArgsConstructor
class Pair<K,V>{
    K key ;
    V value ;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair<K, V> other = (Pair<K, V>) obj;
        return this.key.equals(other.key) && this.value.equals(other.value);
    }
    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
public class DocumentFinderInvertedIndex {
    Map<String, Set<Pair<Integer, Integer>>> invertedIndex  ;
    public DocumentFinderInvertedIndex(){
        this.invertedIndex = new HashMap<>() ;
    }

    public void addDocuments(int documentId, String documents){
        //this method will create the mapping of which word is in which document at which position
        String text = documents.replaceAll("[.,!]", "").toLowerCase() ; //cleaning up the text
        String[] words = text.split(" " );
        for(int i = 0 ; i < words.length; i++ ) {
            invertedIndex.computeIfAbsent(words[i], k -> new HashSet<>()).add(new Pair<>(documentId, i)) ;
        }
        System.out.println();
    }

    //first word , words[0]-> candidates [(docId1, index1), (docId2, index2), (docId3, index3)]
    //                                            |                 |                   |
    //                                            V                 V                   V
    //                                    ( docId, index1 + 1)
    //from the map next indexes will be words[1] -> [(docId, index), (docId, index), (docId, index)]
    public List<Integer> search(String phrase){
        String phraseWords[] = phrase.toLowerCase().split(" ") ;
        Set<Pair<Integer, Integer>> candidates = invertedIndex.getOrDefault(phraseWords[0], new HashSet<>());

        for(int i = 1 ; i < phraseWords.length ; i++){
            Set<Pair<Integer, Integer>> newCandidates = new HashSet<>() ;
            Set<Pair<Integer, Integer>> phraseWordIdx = invertedIndex.getOrDefault(phraseWords[i], new HashSet<>()) ;

            for(Pair<Integer, Integer> pair: candidates){
                Pair<Integer, Integer> nextWordIdx = new Pair<>(pair.getKey(), pair.getValue() + 1) ;
                if(phraseWordIdx.contains(nextWordIdx)){
                    newCandidates.add(nextWordIdx) ;
                }
            }
            candidates = newCandidates ;
        }

        Set<Integer> matchingDocs = new HashSet<>() ;
        for(Pair<Integer, Integer> wordIdx: candidates){
            matchingDocs.add(wordIdx.getKey()) ;
        }

        return new ArrayList<>(matchingDocs) ;
    }

    public static void main(String[] args) {
        DocumentFinderInvertedIndex finder =  new DocumentFinderInvertedIndex() ;
        finder.addDocuments(1, "Cloud computing is the on-demand availability of computer system resources.");
        finder.addDocuments(2, "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems.");
        finder.addDocuments(3, "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers.");

        System.out.println(finder.search("cloud"));
        System.out.println(finder.search("cloud monitoring"));
        System.out.println(finder.search("cloud computing is"));

    }
}
