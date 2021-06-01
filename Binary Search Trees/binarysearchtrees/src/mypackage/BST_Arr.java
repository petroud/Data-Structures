package mypackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;
/**
 * This class implements a Binary Search Tree
 * using static memory allocation before its construction
 * 
 * It provides the fundamental methods of a BST
 * for key insertion, key search, traversal, inrange search 
 * 
 * It uses three static arrays for storing the information 
 * of each node and the in-between connections
 * 
 * For the insertion of new keys in the tree it uses
 * an imaginary stack to mark the availability of the empty 
 * array fields 
 * 
 * @author Dimitris Petrou | 2018030070 | ece@tuc
 *
 */
@SuppressWarnings("all")
public class BST_Arr {
	
	/**
	 * The array for storing the keys of the nodes
	 * 
	 */
	private int[] info;
	
	/**
	 * The array for storing references to left hand side children of the nodes
	 * 
	 */
	private int[] left;
	
	/**
	 * The array for storing references to right hand side children of the nodes
	 * 
	 */
	private int[] right;
	
	/**
	 * The single dimensional sorted array used for the testing 
	 * 
	 */
	private int[] sortedArray;
	
	/**
	 * An iterator used for storing the keys in the sorted array
	 * 
	 */
	private int iterator;
	
	/**
	 * A member variable for storing the pos of the root in the array
	 */
	private int root;
	
	/**
	 * A member variable which keeps track of the next available position in the arrays
	 */
	private int avail;
	
	/**
	 * A member variable corresponding to the size of the tree to be constructed
	 */
	private int treeSize;
	
	/**
	 * A conventional null value that equals to -1
	 */
	private static final int NULL = -1;
	
	
	/**
	 * A member variable used to track the number of comparisons performed by each BST method
	 * 
	 */
	private int comparisons;
		
	
	/**
	 * Class Construcot with int argument
	 * @param ts : the numbers of nodes of the tree
	 */
	public BST_Arr(int ts) {
		this.treeSize = ts;
		
		this.info = new int[treeSize];
		this.left = new int[treeSize];
		this.right = new int[treeSize];
		
		this.sortedArray = new int[treeSize];
		this.iterator = 0;
		
		this.root=NULL;
		this.avail=-1;
		this.comparisons = 0;
		
		setupStack();	
		setupTable();
	}
	
	
	//Availability stack methods
	
	
	/**
	 * Method, initializing the first available position in the arrays to be
	 * the 0
	 */
	public void setupStack() {		
		setNextAvail(0);
	}
	
	/**
	 * Method, initializing the fields of the arrays to be NULL
	 */
	public void setupTable() {
		for(int i=0; i<treeSize; i++) {
			setInfo(i,NULL);
			setLeftChild(i,NULL);
			setRightChild(i,NULL);
		}
	}
	
	/**
	 * Ìethod that returns the number of comparisons.
	 * It is used after each BST-type method call
	 * @return int the number of comparisons performed according to the member variable
	 */
	public int getComparisons() {
		return this.comparisons;
	}
	
	/**
	 * Method that updates the availability of the cells of the array
	 * It "eats" one cell and it returns its pos. Then it updates the avail
	 * variable to show to the next available cell 
	 * @return int the pos of the last available cell
	 */
	public int useAvailCell() {	
		if(this.avail == this.treeSize) {
			this.avail=NULL;
			return NULL;
		}else {
			int curAvail = avail;
			avail++;
			return curAvail;
		}	
	}
	
	public int getNextAvail() {
		return this.avail;
	}

	public void setNextAvail(int i) {
		this.avail=i;
	}
	
	//Root methods
	
	public int getRoot() {
		return this.root;
	}
	
	public void setRoot(int i) {
		this.root = i;
	}
	
	//Node methods
	
	/**
	 * Method that stores a node at a specific position in the arrays.
	 * A node consists of a key and two references to children
	 * @param pos : the index at which the node will be stored
	 * @param in : the key of the node
	 * @param lft : the reference to the left child
	 * @param rgt : the reference to the right child
	 */
	public void storeNode(int pos, int in, int lft, int rgt) {
		this.info[pos] = in;
		this.left[pos] = lft;
		this.right[pos] = rgt;
	}
	
	/**
	 * Method that checks if a cell at a specific index in the arrays
	 * is a valid tree node
	 * @param i : The index to check if it is a node
	 * @return boolean: true/false
	 */
	public boolean isNode(int i) {
		try {
			if(getInfo(i)!=NULL) 
				return true;
		}catch(Exception e) {}
		
		return false;
	}

	public void setInfo(int i, int info) {
		this.info[i]=info;
	}
	
	public void setLeftChild(int i, int child) {
		this.left[i] = child;
	}
	
	public void setRightChild(int i, int child) {
		this.right[i] = child;
	}
	
	public int getInfo(int i) {
		int inf = NULL;
		
		try {
			inf = this.info[i];
			return inf;
		}catch(Exception e) {
			return NULL;
		}
	}
	
	public int getLeftChild(int i) {
		try {
			return this.left[i];
		}catch(Exception e) {
			return NULL;
		}
	}
	
	public int getRightChild(int i) {
		try {
			return this.right[i];
		}catch(Exception e) {
			return NULL;
		}
	}
	
	/**
	 * Method that checks if a node has a left child
	 * @param p The index of the node
	 * @return true/false
	 */
	public boolean hasLeftChild(int p) {
		if(left[p]!=NULL) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that checks if a node has a right child
	 * @param p The index of the node
	 * @return true/false
	 */
	public boolean hasRightChild(int p) {
		if(right[p]!=NULL && isNode(p)) {
			return true;
		}
		return false;
	}

	
	
	//Tree Methods
	
	/**
	 * Method for inserting a key in the static BST
	 * 
	 * First it requests a position to store the new node
	 * If the useAvailCell() method returns NULL then the tree is full
	 * and the insertion process ends there.
	 * 
	 * Then it checks if the Tree has a root, if not it inserts the 
	 * new key as root node.
	 * 
	 * Then iteratively we move through the nodes of the tree
	 * according to the value of the new key. 
	 * 
	 * If we find a node that fits out requirements we check if it 
	 * has a children at the required position. If it has, we keep moving, 
	 * if not we insert the new key there and we store in the arrays.
	 * 
	 * @param key The key to be inserted
	 */
	public void insertKey(int key) {
		int pos = useAvailCell();
		this.comparisons=0;
		
		if(pos==NULL) {
			System.out.println("--> Tree Full!");
			return;
		}
		
		if(this.root == NULL) {
			this.root = pos;
			storeNode(pos,key,NULL,NULL);
			return;
		}
		
		int x = this.root;
		
		while(true) {
			if(key < getInfo(x)) {
				this.comparisons++;
				
				if(hasLeftChild(x)) {
					x = getLeftChild(x);
					continue;
				}else {
					setLeftChild(x,pos);
					storeNode(pos,key,NULL,NULL);
					break;
				}
				
			}else {
				this.comparisons++;
				
				if(hasRightChild(x)) {
					x = getRightChild(x);
					continue;
				}else {
					setRightChild(x,pos);
					storeNode(pos,key,NULL,NULL);
					break;
				}
			}
		}
	}
	
	
	public void inorder() {
		inorderRec(root);
	}
	
	/**
	 * Recursive method to traverse the tree
	 * in an inorder way. We first recur down the left
	 * hand side and as we return we print the keys.
	 * Then we print the keys and we recur down the right
	 * hand side.
	 * @param rt The root of the tree
	 */
	private void inorderRec(int rt) {
		if(rt!=NULL) {
			inorderRec(getLeftChild(rt));
			System.out.print(getInfo(rt)+"   ");
			inorderRec(getRightChild(rt));
		}
	}
	
	
	/**
	 * Method that returns the sorted array outside the class
	 * @return
	 */
	public int[] inorderToArray() {
		inorderToArrayRec(root);
		return sortedArray;
	}
	
	/**
	 * Method that is used add keys in the sorted array 
	 * @param key:  the new key to be inserted
	 */
	public void addInSortedArray(int key){
		sortedArray[this.iterator] = key;
		this.iterator++;
	}
	
	/**
	 * Method that performs an inorder 
	 * traversal in the tree and it recursively stores
	 * the sorted keys in the the single dimensional array
	 * @param rt
	 */
	private void inorderToArrayRec(int rt) {
		if(rt!=NULL) {
			inorderToArrayRec(getLeftChild(rt));
			addInSortedArray(getInfo(rt));
			inorderToArrayRec(getRightChild(rt));
		}
	}
	
	/**
	 * Method that searches for a specific key in the tree
	 * 
	 * Iterativaly, it moves through the nodes of the tree.
	 * First it checks if the curNode is NULL, which means
	 * that we currently are at a non node index and the 
	 * search ends there.
	 * 
	 * If the key equals the info of the current node, then
	 * the key was found in the tree and the process ends there.
	 * 
	 * If the key is greater than the current node then the search 
	 * continues to the right, else the search continues to the left.
	 * 
	 * The comparisons between nodes are counted appropriately 
	 * @param key 
	 * @return true/false if the key was found or not
	 */
	public boolean searchKey(int key) {
		/**
		 * The node at which we currently are, we begin from the root.
		 */
		int curNode = this.root;
		this.comparisons=0;
		
		while(true) {
			if(curNode==NULL) {
				break;
			}
			if(getInfo(curNode)==key) {
				this.comparisons++;
				return true;
			}else if(getInfo(curNode)>key) {
				curNode = getLeftChild(curNode);
				this.comparisons++;
				continue;
			}else {
				curNode = getRightChild(curNode);
				this.comparisons++;
				continue;
			}
		}
		return false;
	}
	
	public void inrange(int a, int b) {
		this.comparisons = 0;
		inrangeRec(root, a, b);
	}
	

	/**
	 * Recursive method that searches keys within a specific range.
	 * 
	 * We begin from the root and every time we check if the @param node
	 * is a valid node, if not we return
	 * 
	 * If the lower bound of the range is less than the key of the current node
	 * the search continues to the left
	 * 
	 * If the info of the current node is between the lower and upper bound
	 * then it is a node that we are interested for it. We print its info
	 * 
	 * If the upper bound of the range is greater than the key of the current node
	 * the search continues to the right.
	 * 
	 * The comparisons between nodes are counted appropriately 
	 * 
	 * @param node The current node 
	 * @param a The lower bound
	 * @param b The upper bound
	 */
	private void inrangeRec(int node, int a, int b) {		
		if (!isNode(node)) { 
            return; 
        } 
  
        /* Since the desired o/p is sorted, recurse for left subtree first 
         If root->data is greater than k1, then only we can get o/p keys 
         in left subtree */
        if (a < getInfo(node)) { 
        	this.comparisons++;
            inrangeRec(getLeftChild(node), a, b); 
        } 
  
        /* if root's data lies in range, then prints root's data */
        if (a <= getInfo(node) && b >= getInfo(node)) {
        	this.comparisons+=2;
            //System.out.print(getInfo(node) + " "); 
        } 
  
        /* If root->data is smaller than k2, then only we can get o/p keys 
         in right subtree */
        if (b > getInfo(node)) {
        	this.comparisons++;
            inrangeRec(getRightChild(node), a, b); 
        } 
		
	}
	
	/**
	 * Method that prints all the tree's info and nodes
	 * 
	 */
	public void printTree() {
		System.out.println("\n");
		for(int i = 0 ; i<treeSize; i++) {
			System.out.println("------------------ Node "+i+" ------------------");
			System.out.println(" Info:    "+ info[i]);
			System.out.println(" Left:    "+ left[i]);
			System.out.println(" Right:   "+ right[i]);
			System.out.println("---------------------------------------------\n");
		}
	}
}	
	
