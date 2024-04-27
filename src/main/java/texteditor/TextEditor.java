package texteditor;


class Node {
    public final char value ;
    public Node next, prev ;
    public Node(final char value){
        this.value = value ;
    }
}

class DoublyLinkedList {
    Node head, tail ;
    public DoublyLinkedList(){
        this.head =  new Node('\0') ;
        this.tail =  new Node('\0') ;
        this.head.next = this.tail ;
        this.tail.prev = this.head ;
    }

    public void addLast(final Node node){
        node.prev = this.tail.prev ;
        node.next = this.tail ;
        this.tail.prev.next =  node ;
        this.tail.prev = node ;
    }

    public void addFirst(final Node node){
        node.next = this.head.next ;
        node.prev = this.head ;
        this.head.next.prev = node ;
        this.head.next = node ;
    }

    public Node removeLast(){
        if(this.isEmpty()){
            return null ;
        }
        Node toRemove = this.tail.prev ;

        toRemove.prev.next = this.tail ;
        this.tail.prev = toRemove.prev ;

        return toRemove ;
    }

    public Node removeFirst(){
        if(this.isEmpty()){
            return null ;
        }
        Node toRemove = this.head.next ;

        this.head.next =  toRemove.next ;
        toRemove.next.prev = this.head ;

        return toRemove ;
    }

    public boolean isEmpty(){
        return this.head.next == this.tail ;
    }

    public String lastChars(int k){
        StringBuilder sb = new StringBuilder() ;
        Node curr = this.tail.prev ;
        for(int i = 0 ; i < k && curr != this.head; i++){
            sb.append(curr.value) ;
            curr = curr.prev ;
        }
        sb.reverse();
        return sb.toString();
    }
}
class TextEditor {
    private final DoublyLinkedList left, right ;
    public TextEditor() {
        this.left = new DoublyLinkedList() ;
        this.right = new DoublyLinkedList() ;
    }

    public void addText(String text) {
        for(int i = 0; i < text.length(); i++){
            left.addLast(new Node(text.charAt(i)));
        }
    }

    public int deleteText(int k) {
        int i = 0 ;
        while(!this.left.isEmpty() && i < k){
            left.removeLast() ;
            ++i ;
        }
        return  i ;
    }

    public String cursorLeft(int k) {
        for(int i = 0 ; i< k && !this.left.isEmpty() ; i++){
            this.right.addFirst(this.left.removeLast());
        }
        return this.left.lastChars(10) ;
    }

    public String cursorRight(int k) {
        for(int i = 0 ; i< k && !this.right.isEmpty() ; i++){
            this.left.addLast(this.right.removeFirst());
        }
        return this.left.lastChars(10) ;
    }
}

/**
 * Your TextEditor object will be instantiated and called as such:
 * TextEditor obj = new TextEditor();
 * obj.addText(text);
 * int param_2 = obj.deleteText(k);
 * String param_3 = obj.cursorLeft(k);
 * String param_4 = obj.cursorRight(k);
 */
