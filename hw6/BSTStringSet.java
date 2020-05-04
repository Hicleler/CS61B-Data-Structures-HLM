import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * Implementation of a BST based String Set.
 * @author
 */


public class BSTStringSet implements StringSet, Iterable<String> {
    /** Root node of the tree. */
    private Node _root;
    private BSTStringSet left;
    private BSTStringSet right;

    /** Creates a new empty set. */
    public BSTStringSet() {
        _root = null;
    }

    @Override
    public void put(String s) {
        if(_root == null) {
            _root = new Node(s);
        } else if(s.compareTo(_root.s)<0){
            if(_root.left!=null){
                left.put(s);
            } else {
                left = new BSTStringSet();
                _root.left = new BSTStringSet.Node(s);
                left._root = _root.left;
            }
        } else if(s.compareTo(_root.s)>0){
            if(_root.right!=null){
                right.put(s);
            } else {
                right = new BSTStringSet();
                _root.right = new BSTStringSet.Node(s);
                right._root = _root.right;
            }
        } else {
            return;
        }
    }

    @Override
    public boolean contains(String s) {
        return contains(s, _root);
    }

    public boolean contains(String s, Node n){
        if(n==null){
            return false;
        }
        if(s.compareTo(n.s)<0){
            return contains(s, n.left);
        }

        if(s.compareTo(n.s)>0){
            return contains(s, n.right);
        }
        return true;
    }

    @Override
    public List<String> asList() {
        List<String> out = new ArrayList<>();
        for (String s : this) {
            out.add(s);
        }
        return out;
    }

    private Node put(String s, Node n){
        if(n==null) {
            return new Node(s);
        }

        if(s.compareTo(n.s)<0){
            n.left = put(s, n.left);
        }

        if(s.compareTo(n.s)>0){
            n.right=put(s, n.right);
        }
        return n;
    }


    /** Represents a single Node of the tree. */
    private static class Node {
        /** String stored in this Node. */
        private String s;
        /** Left child of this Node. */
        private Node left;
        /** Right child of this Node. */
        private Node right;

        /** Creates a Node containing SP. */
        Node(String sp) {
            s = sp;
        }

    }

    /** An iterator over BSTs. */
    private static class BSTIterator implements Iterator<String> {
        /** Stack of nodes to be delivered.  The values to be delivered
         *  are (a) the label of the top of the stack, then (b)
         *  the labels of the right child of the top of the stack inorder,
         *  then (c) the nodes in the rest of the stack (i.e., the result
         *  of recursively applying this rule to the result of popping
         *  the stack. */
        private Stack<Node> _toDo = new Stack<>();

        /** A new iterator over the labels in NODE. */
        BSTIterator(Node node) {
            addTree(node);
        }

        @Override
        public boolean hasNext() {
            return !_toDo.empty();
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Node node = _toDo.pop();
            addTree(node.right);
            return node.s;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /** Add the relevant subtrees of the tree rooted at NODE. */
        private void addTree(Node node) {
            while (node != null) {
                _toDo.push(node);
                node = node.left;
            }
        }
    }

    @Override
    public Iterator<String> iterator() {
        return new BSTIterator(_root);
    }

    public Iterator<String> iterator(String low, String high) {
        return new Helper(low, high, _root);
    }

    private static class Helper implements Iterator<String> {
        private Stack<Node> s = new Stack<>();
        String h;
        String l;

        Helper(String low, String high, Node root) {
            l = low;
            h = high;
            helper(root);
        }

        @Override
        public boolean hasNext() {
            return (!s.empty() && s.peek().s.compareTo(h) <= 0);
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node node = s.pop();
            helper(node.right);
            return node.s;
        }

        private void helper(Node node) {
            while (node != null && node.s.compareTo(l) >= 0) {
                s.push(node);
                node = node.left;
            }
            if (node != null) {
                helper(node.right);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }


}}
