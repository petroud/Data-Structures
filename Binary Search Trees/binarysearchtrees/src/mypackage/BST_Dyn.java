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
		root = insertRec(root,key);
	}
	
	private Node insertRec(Node root, int key) {
		this.comparisons=0;

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

}
