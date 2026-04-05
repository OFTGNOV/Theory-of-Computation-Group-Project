/**
 * Tamai Richards
 * March 30, 2026
 */
package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a parse tree built during parsing.
 * The tree shows the hierarchical structure of the parsed input.
 */
public class ParseTree {
    private Node root;

    /**
     * Creates an empty parse tree.
     */
    public ParseTree() {
        this.root = null;
    }

    /**
     * Sets the root node of the parse tree.
     *
     * @param root the root node
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * Returns the root node of the parse tree.
     *
     * @return the root node, or null if tree is empty
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Returns true if the tree is empty.
     *
     * @return true if the tree has no root
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Prints the tree in an indented text format.
     */
    public void print() {
        if (root != null) {
            root.print(0);
        }
    }

    /**
     * Represents a node in the parse tree.
     */
    public static class Node {
        private String label;
        private String value;
        private List<Node> children;

        /**
         * Creates a new node with the given label.
         *
         * @param label the node label (e.g., "Assignment", "BinOp")
         */
        public Node(String label) {
            this.label = label;
            this.value = null;
            this.children = new ArrayList<>();
        }

        /**
         * Creates a new node with label and value.
         *
         * @param label the node label
         * @param value the node value (e.g., "x", "5", "+")
         */
        public Node(String label, String value) {
            this.label = label;
            this.value = value;
            this.children = new ArrayList<>();
        }

        /**
         * Adds a child node to this node.
         *
         * @param child the child node to add
         */
        public void add(Node child) {
            children.add(child);
        }

        /**
         * Returns the label of this node.
         *
         * @return the node label
         */
        public String getLabel() {
            return label;
        }

        /**
         * Returns the value of this node.
         *
         * @return the node value, or null if not set
         */
        public String getValue() {
            return value;
        }

        /**
         * Returns the children of this node.
         *
         * @return list of child nodes
         */
        public List<Node> getChildren() {
            return children;
        }

        /**
         * Prints this node and its children with indentation.
         *
         * @param indent the current indentation level
         */
        public void print(int indent) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                sb.append("  ");
            }
            sb.append(label);
            if (value != null) {
                sb.append(" : ").append(value);
            }
            System.out.println(sb.toString());

            for (Node child : children) {
                child.print(indent + 1);
            }
        }

        /**
         * Returns a string representation of this node.
         *
         * @return string representation
         */
        @Override
        public String toString() {
            if (value != null) {
                return label + "(" + value + ")";
            }
            return label;
        }
    }
}
