package functionFinder;


import java.util.*;

class Node {
    String argType ;
    List<String> variadicFunction ;
    List<String> nonVariadicFunction ;
    Map<String, Node> children ;
    public Node(String argType){
        this.argType = argType ;
        this.variadicFunction = new ArrayList<>() ;
        this.nonVariadicFunction = new ArrayList<>() ;
        this.children = new HashMap<>() ;
    }
}


public class FunctionLibrary {
    Node root  = new Node("root") ;

    private boolean allSame(String arg, int start, List<String> arguments){
        for(int i= start; i < arguments.size(); i++){
            if(!arguments.get(i).equals(arg))
                return false ;
        }
        return true ;
    }
    public void register(String funcName, List<String> arguments, boolean isVariadic){
        Node curr = root ;
        for(String arg: arguments){
            if(!curr.children.containsKey(arg))
                curr.children.put(arg, new Node(arg)) ;
            curr = curr.children.get(arg) ;
        }
        if (isVariadic)
            curr.variadicFunction.add(funcName) ;
        else
            curr.nonVariadicFunction.add(funcName) ;
    }

    public List<String> match(List<String> arguments){
        List<String> result = new ArrayList<>() ;
        Node cur = root ;
        for(int i = 0 ; i < arguments.size(); i++){
            String arg = arguments.get(i) ;
            cur = cur.children.get(arg) ;
            if(cur==null)
                return result ;
            if(i!= arguments.size() -1 && !cur.variadicFunction.isEmpty() && allSame(arg, i + 1, arguments))
                result.addAll(cur.variadicFunction) ;
            if(i == arguments.size() - 1){
                result.addAll(cur.variadicFunction) ;
                result.addAll(cur.nonVariadicFunction) ;
            }
        }
        return result ;
    }


    public static void main(String[] args) {
        FunctionLibrary library = new FunctionLibrary() ;
        library.register("FuncA", Arrays.asList("String", "Integer", "Integer"), false);
        library.register("FuncB", Arrays.asList("String", "Integer"), true);
        library.register("FuncC", List.of("Integer"), true);
        library.register("FuncD", Arrays.asList("Integer", "Integer"), true);
        library.register("FuncE", Arrays.asList("Integer", "Integer", "Integer"), false);
        library.register("FuncF", List.of("String"), false);
        library.register("FuncG", List.of("Integer"), false);

        System.out.println(library.match(Arrays.asList("String")));
        System.out.println(library.match(Arrays.asList("Integer")));
        System.out.println(library.match(Arrays.asList("Integer", "Integer", "Integer", "Integer")));
        System.out.println(library.match(Arrays.asList("Integer", "Integer", "Integer")));
        System.out.println(library.match(Arrays.asList("String", "Integer", "Integer", "Integer")));
        System.out.println(library.match(Arrays.asList("String", "Integer", "Integer")));
    }
}
