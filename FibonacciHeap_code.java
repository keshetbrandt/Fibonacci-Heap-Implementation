
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap {
    public HeapNode first;
    public HeapNode min;
    public int roots;
    public int size;
    public int numMarks;
    public static int links;
    public static int cuts;


    public FibonacciHeap() {
        this.first = null;
        this.min = null;
        this.roots = 0;
        this.size = 0;
        this.numMarks = 0;
    }

    /**
     *
     * return the first element in the heap - the newest element
     */

    public HeapNode getFirst() {
        return this.first;
    }

    /**
     * public boolean isEmpty()
     * <p>
     * Returns true if and only if the heap is empty.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     * <p>
     * Returns the newly created node.
     */
    public HeapNode insert(int key) {
        HeapNode newNode = new HeapNode(key);
        if (this.isEmpty()) {
            this.first = newNode;
            this.min = newNode;
            this.roots = 1;
            this.size = 1;
            newNode.setNext(newNode);
            newNode.setPrev(newNode);
        } else {
            newNode.setPrev(this.first.getPrev());
            this.first.getPrev().setNext(newNode);
            this.first.setPrev(newNode);
            newNode.setNext(this.first);
            this.first = newNode;
            this.roots++;
            this.size++;
            if (min.getKey() > key) {
                this.min = newNode;
            }
        }
        return newNode;
    }

    /**
     * public void deleteMin()
     * <p>
     * Deletes the node containing the minimum key.
     */
    public void deleteMin() {
        if(this.isEmpty()){
            return;
        }
        if(this.size == 1){
            this.min = null;
            this.first=null;
            this.size=0;
            this.roots=0;
            this.numMarks=0;
            return;
        }
        if(this.min.getRank() == 0) {
            if (this.min == this.first) {
                this.first = min.getNext();
             }
            this.min.getNext().setPrev(min.getPrev());
            this.min.getPrev().setNext(min.getNext());
        }
        else {
            HeapNode temp = min.getChild();
            temp.setPrev(min.getPrev());
            min.getPrev().setNext(temp);
            for (int i = 0; i < min.getRank(); i++) {
                temp.setParent(null);
                this.roots++;
                if(temp.getMarked()) {
                    temp.setMarked(false);
                    this.numMarks--;
                }
                if(i != min.getRank()-1) {
                    temp = temp.getNext();
                }
            }

            if(this.min==this.first){
                this.first = temp.getNext();
            }
            temp.setNext(min.getNext());
            min.getNext().setPrev(temp);
        }
        this.size--;
        this.roots--;
        this.SucLink();
    }

    /**
     * public void SucLink()
     * <p>
     * Full consolidation of the heap
     */

    public void SucLink() {
        if (this.roots > 1) {
            HeapNode[] basket = new HeapNode[(int) (2 * Math.log(this.size) / Math.log(2)) + 1];
            HeapNode temp = this.first;
            int originalRoots = this.roots;
            for (int i = 0; i < originalRoots; i++) {
                int rank = temp.getRank();
                if (basket[rank] == null) {
                    basket[rank] = temp;
                    temp = temp.getNext();
                } else {
                    HeapNode place = temp.getNext();
                    HeapNode linked = this.Link(temp, basket[rank]);
                    basket[rank] = null;
                    while (basket[linked.getRank()] != null) {
                        HeapNode curLink = basket[linked.getRank()];
                        basket[linked.getRank()] = null;
                        linked = this.Link(linked, curLink);
                    }
                    basket[linked.getRank()] = linked;
                    temp = place;
                }
            }
            HeapNode cur = null;
            this.roots = 0;
            int last = 0;
            for (int i = 0; i < basket.length; i++) {
                if (basket[i] != null) {
                    if (cur == null) {
                        cur = basket[i];
                        last = i;
                        this.min = cur;
                        this.roots++;
                    } else {
                        if (min.getKey() > basket[i].getKey()) {
                            this.min = basket[i];
                        }
                        cur.setNext(basket[i]);
                        basket[i].setPrev(cur);
                        cur = cur.getNext();
                        this.roots++;
                    }
                }
            }
            basket[last].setPrev(cur);
            cur.setNext(basket[last]);
            this.first = basket[last];
        }
        else{
            if(this.isEmpty()){
                this.first=null;
                this.roots=0;
                this.numMarks=0;
                this.size=0;
                this.min=null;
            }
            else{
                if (this.first == this.min) {
                    this.first = this.first.getNext();
                }
                this.first.setPrev(this.first);
                this.first.setNext(this.first);
                this.min=this.first;
            }
        }
    }

    /**
     * public HeapNode Link(HeapNode root1, HeapNode root2)
     * <p>
     * Returns a linked HeapNode of root1 and root2
     */

    public HeapNode Link(HeapNode root1, HeapNode root2) {
        if (root1.getKey() > root2.getKey()) { // We want root1 to point to the smaller key
            HeapNode temp = root1;
            root1 = root2;
            root2 = temp;
        }
        HeapNode child = root1.getChild();
        root1.setChild(root2);
        root2.setParent(root1);
        root2.getPrev().setNext(root2.getNext());
        root2.getNext().setPrev(root2.getPrev());
        if (child != null) {
            root2.setPrev(child.getPrev());
            child.getPrev().setNext(root2);
            root2.setNext(child);
            child.setPrev(root2);
        } else {
            root2.setNext(root2);
            root2.setPrev(root2);
        }
        this.roots--;
        root1.setRank(root1.getRank() + 1);
        links++;
        return root1;
    }

    /**
     * public HeapNode findMin()
     * <p>
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     */
    public HeapNode findMin() {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Melds heap2 with the current heap.
     */
    public void meld(FibonacciHeap heap2) {
        if (!this.isEmpty() && !heap2.isEmpty()) {
            HeapNode temp = this.first.getPrev();
            HeapNode temp2 = heap2.first.getPrev();
            temp.setNext(heap2.first);
            heap2.first.setPrev(temp);
            temp2.setNext(this.first);
            this.first.setPrev(temp2);
            if (this.min.getKey() > heap2.min.getKey()) {
                this.min = heap2.min;
            }
        } else {
            if (this.isEmpty()) {
                this.min = heap2.min;
                this.first=heap2.first;
            }
        }
        this.roots = this.roots + heap2.roots;
        this.size = this.size + heap2.size();
        this.numMarks = this.numMarks + heap2.numMarks;
        heap2.first = this.first;
        heap2.min = this.min;
        heap2.roots = this.roots;
        heap2.size = this.size;
        heap2.numMarks = this.numMarks;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of elements in the heap.
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * (Note: The size of of the array depends on the maximum order of a tree.)
     */
    public int[] countersRep() {
        if(this.isEmpty()){
            return new int [0];
        }
        int[] arr = new int[this.size];
        int bigI = 0;
        HeapNode temp = this.first;
        arr[temp.getRank()]++;
        if(temp.getRank() > bigI) {
            bigI = temp.getRank();
        }
        temp = temp.getPrev();
        while (temp != this.first) {
            arr[temp.getRank()]++;
            if(temp.getRank() > bigI) {
                bigI = temp.getRank();
            }
            temp = temp.getPrev();
        }
        bigI++;
        int[] res = new int[bigI];
        System.arraycopy(arr, 0, res, 0, bigI);
        return res;
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     */
    public void delete(HeapNode x) {
        decreaseKey(x, Integer.MAX_VALUE);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        if (delta == Integer.MAX_VALUE) {
            this.min = x;
            x.setKey(Integer.MIN_VALUE);
        } else {
            x.setKey(x.getKey() - delta);
            if (x.getKey() < min.getKey()) {
                this.min = x;
            }
        }
        HeapNode parent = x.getParent();
        if (parent != null) {
            if(parent.getKey() > x.getKey()) {
                casCut(x, parent);
            }
        }
    }

    /**
     * public void cut(HeapNode x, HeapNode parent)
     * <p>
     * Detaches node x from its parent and turns it into a root in the heap
     */

    public void cut(HeapNode x, HeapNode parent) {
        x.setParent(null);
        if(x.getMarked()) {
            x.setMarked(false);
            this.numMarks--;
        }
        this.roots++;
        parent.setRank(parent.getRank() - 1);
        if (parent.getChild() == x) {
            if (parent.getRank() > 0) {
                parent.setChild(x.getNext());
            }
            else {
                parent.setChild(null);
            }
        }
        if(parent.getRank() >0) {
            x.getNext().setPrev(x.getPrev());
            x.getPrev().setNext(x.getNext());
        }
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        x.setNext(first);
        x.setPrev(first.getPrev());
        first.getPrev().setNext(x);
        first.setPrev(x);
        this.first = x;
        cuts++;
    }

    /**
     * public void casCut(HeapNode x, HeapNode parent)
     * <p>
     * Cascading cuts starting from node x and its parent
     */

    public void casCut(HeapNode x, HeapNode parent) {
        cut(x, parent);
        if (parent.getParent() != null) {
            if (!parent.getMarked()) {
                parent.setMarked(true);
                this.numMarks++;
            } else {
                casCut(parent, parent.getParent());
            }
        }
    }


    /**
     * public int nonMarked()
     * <p>
     * This function returns the current number of non-marked items in the heap
     */
    public int nonMarked() {
        return (this.size - this.numMarks);
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * <p>
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return (this.roots + 2 * this.numMarks);
    }

    /**
     * public static int totalLinks()
     * <p>
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks() {
        return links;
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return cuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     * <p>
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        if(H.isEmpty()){
            return new int[0];
        }
        FibonacciHeap minHeap = new FibonacciHeap();
        int[] arr = new int[k];
        arr[0] = H.getFirst().getKey();
        HeapNode minNode = H.getFirst();
        for(int i=1; i < k; i++){
            if(minNode.getChild() != null) {
                minNode = minNode.getChild();
                HeapNode started = minNode;
                do {
                    HeapNode copy = minHeap.insert(minNode.getKey());
                    copy.pointer = minNode;
                    minNode = minNode.getNext();
                }
                while(minNode != started);
            }
            minNode = minHeap.findMin().pointer;
            arr[i] = minNode.getKey();
            minHeap.deleteMin();
        }
        return arr;
    }


    /**
     * public class HeapNode
     * <p>
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     */
    public static class HeapNode {

        public int key;
        public boolean marked;
        public HeapNode child;
        public HeapNode parent;
        public HeapNode prev;
        public HeapNode next;
        public int rank;
        public HeapNode pointer; // For Kmin

        public HeapNode(int key) {
            this.key = key;
            this.marked = false;
            this.child = null;
            this.parent = null;
            this.prev = null;
            this.next = null;
            this.rank = 0;
            this.pointer=null;
        }

        /**
         * getField()
         * <p>
         * returns the field
         */

        public int getKey() {
            return this.key;
        }

        public boolean getMarked() {
            return this.marked;
        }

        public HeapNode getChild() {
            return this.child;
        }

        public HeapNode getParent() {
            return this.parent;
        }

        public HeapNode getPrev() {
            return this.prev;
        }

        public HeapNode getNext() {
            return this.next;
        }

        public int getRank() {
            return this.rank;
        }

        /**
         * setField(field)
         * <p>
         * sets the field to the input value
         */

        public void setKey(int key) {
            this.key = key;
        }

        public void setMarked(boolean marked) {
            this.marked = marked;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public void setPrev(HeapNode Prev) {
            this.prev = Prev;
        }

        public void setNext(HeapNode Next) {
            this.next = Next;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

    }

}
