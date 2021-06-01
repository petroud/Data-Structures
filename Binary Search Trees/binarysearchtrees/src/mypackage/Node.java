package mypackage;

/**
 * This class is used to create objects representing 
 * the nodes of the dynamic BST
 * @author Dimitris Petrou
 * @from GeeksForGeeks
 *
 */
public class Node {
	
	/**
	 * The integer key of the node
	 */
	private int key;
	
	/**
	 * A Node type pointer to the left child of the node
	 */
	private Node left;
	
	/**
	 * A Node type pointer to the right child of the node
	 */
	private Node right;
	
	
	/**
	 * Class constructor with int argument
	 * @param i : The key of the node to be created
	 */
	public Node(int i) {
		this.key = i;
		this.left = null;
		this.right = null;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

}
