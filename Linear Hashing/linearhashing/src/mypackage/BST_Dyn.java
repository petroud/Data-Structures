package mypackage;

/**
 * Class that implements a BST with dynamic memory allocation
 * 
 * @author Dimitris Petrou | 2018030070 | ece@tuc (modified)
 * 
 * @from GeeksForGeeks
 * 
 */
public class BST_Dyn {
	
	private Node root;
	private int comparisons;
	
	public BST_Dyn() {
		this.root = null;
		this.comparisons = 0;
	}
	
	public int getComparisons() {
		return this.comparisons;
	}
	
	public void insertKey(int key) {
		this.comparisons=0;
		root = insertRec(root,key);
	}
	
	private Node insertRec(Node root, int key) {

		if (root == null) { 
            root = new Node(key); 
            return root; 
        } 
		this.comparisons++;
  
        /* Otherwise, recur down the tree */
        if (key < root.getKey()) { 
            root.setLeft(insertRec(root.getLeft(), key));
        }
        else if (key > root.getKey()) {
            root.setRight(insertRec(root.getRight(), key)); 
        }
		this.comparisons+=2;

        /* return the (unchanged) node pointer */
        return root; 
	}
	
	public void inorder() {
		inorderRec(root);
	}
	
	private void inorderRec(Node root) { 
        if (root != null) { 
            inorderRec(root.getLeft()); 
            System.out.print(root.getKey()+"   "); 
            inorderRec(root.getRight()); 
        } 
    } 
	
	public boolean searchKey(int searchKey){
		
		Node current = root;
		this.comparisons=0;
		
		while(current!=null){
			if(current.getKey()==searchKey){
				this.comparisons++;
				return true;
			}else if(current.getKey()>searchKey){
				current = current.getLeft();
				this.comparisons++;
			}else{
				current = current.getRight();
				this.comparisons++;
			}
		}
		return false;
	}
	
	public void inrange(int k1, int k2) {
		this.comparisons = 0;
		inrangeRec(root, k1, k2);
	}
	
	private void inrangeRec(Node node, int k1, int k2) {	          
        /* base case */
	    if (node == null) { 
	    	return; 
        } 
  
        /* Since the desired o/p is sorted, recurse for left subtree first 
         If root->data is greater than k1, then only we can get o/p keys 
         in left subtree */
        if (k1 < node.getKey()) { 
        	this.comparisons++;
            inrangeRec(node.getLeft(), k1, k2); 
        } 
  
        /* if root's data lies in range, then prints root's data */
        if (k1 <= node.getKey() && k2 >= node.getKey()) { 
        	this.comparisons+=2;
        	//System.out.print(node.getKey() + " "); 
        } 
  
        /* If root->data is smaller than k2, then only we can get o/p keys 
         in right subtree */
        if (k2 > node.getKey()) { 
        	this.comparisons++;
        	inrangeRec(node.getRight(), k1, k2); 
        } 
	}
	
	 public boolean delete(int value){
			this.comparisons = 0;

		    Node current = root;
	        Node parent = root;
	        boolean isLeftChild = false;
	        
	        while(current.key != value){
	        	this.comparisons++;
	            parent = current;
	            if(value < current.key){
		        	this.comparisons++;
	                // Move to the left if searched value is less
	                current = current.left;
	                isLeftChild = true;
	            }
	            else{
	                // Move to the right if searched value is >=
	                current = current.right;
	                isLeftChild = false;
	            }
	            if(current == null){
		        	this.comparisons++;
	                return false;
	            }
	        }
	        // if reached here means node to be deleted is found
	        
	        // Leaf node deletion case
	        if(current.left == null && current.right == null){
	        	this.comparisons+=2;
	            // if root node is to be deleted
	        	this.comparisons+=2;
	            if(current == root){
	                root = null;
	            }
	            // left child
	            else if(isLeftChild){
	                parent.left = null;
	            }
	            // right child
	            else{
	                parent.right = null;
	            }
	        }
	        // Node to be deleted has one child case
	        // Node to be deleted has right child
	        else if(current.left == null){
	        	this.comparisons++;
	            // if root node is to be deleted
	            if(current == root){
		        	this.comparisons++;
	                root = current.right;
	            }
	            // if deleted node is left child
	            else if(isLeftChild){
		        	this.comparisons++;
	                parent.left = current.right;
	            }
	            // if deleted node is right child
	            else{
		        	this.comparisons++;
	                parent.right = current.right;
	            }
	        }
	        // Node to be deleted has left child
	        else if(current.right == null){
	        	this.comparisons+=2;
	            if(current == root){
	                root = current.left;
	            }
	            // if deleted node is left child
	            else if(isLeftChild){
	                parent.left = current.left;
	            }
	            // if deleted node is right child
	            else{
	                parent.right = current.left;
	            }
	        }
	        // Node to be deleted has two children case
	        else{
	            // find in-order successor of the node to be deleted
	            Node successor = findSuccessor(current);
	        	this.comparisons+=2;
	            if(current == root){
	                root = successor;
	            }
	            // if deleted node is left child
	            else if(isLeftChild){
	                parent.left = successor;
	            }
	            // if deleted node is right child
	            else{
	                parent.right = successor;
	            }
	            successor.left = current.left;
	        }
	        return true;
	    }
	    // Method to find the in-order successor of the deleted node
	    private Node findSuccessor(Node node){
	        Node successor = node;
	        Node successorParent = node;
	        // Start from the right child of the node to be deleted
	        Node current = node.right;
        	this.comparisons+=2;
	        while(current != null){
	            successorParent = successor;
	            successor = current;
	            current = current.left;
	        }
	        // When In-order successor is in the left subtree 
	        // perform two ref changes here as we have 
	        // access to successorParent
	        if(successor != node.right){
	            successorParent.left = successor.right;
	            // applicable only when successor is not right child
	            // so doing here
	            successor.right = node.right;
	        }
	        return successor;
	    }

}
