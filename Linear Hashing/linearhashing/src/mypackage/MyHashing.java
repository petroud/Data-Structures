package mypackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class MyHashing {
	
	/**
	 * A FileAccess object to read the file
	 */
	private FileAccess faccess;
	
	/**
	 * A Vector to store the numbers read from the file
	 */
	private Vector<Integer> nbrs;
	
	/**
	 * A Linear Hashing instance with 80% max load factor
	 */
	private LinearHashing myTable80;
	
	/**
	 * A Linear Hashing instance with 50% max load factor
	 */
	private LinearHashing myTable50;
	
	/**
	 * A dynamic BST object
	 */
	private BST_Dyn myTree;
	
	/**
	 * Number of buckets in the table
	 */
	private final int numberOfBuckets = 100;
	
	/**
	 * Keys in every bucket
	 */
	private final int keysInBucket = 10;
	
	
	public MyHashing(String path) {
		initializeFile(path);
		constructHashTable();
	}
	
	public void initializeFile(String path) {
		faccess = new FileAccess(path);
		nbrs = faccess.getNumbers();
	}
	
	/**
	 * Instantiation of the tables and the tree
	 */
	public void constructHashTable() {
		myTable80 = new LinearHashing(keysInBucket, numberOfBuckets);
		myTable50 = new LinearHashing(keysInBucket, numberOfBuckets);
		myTable50.setLoadFactor(0.5f);
		
		myTree = new BST_Dyn();
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		printLayout();
		
		try {
			checkFile(args[0]);
		}catch(Exception e) {
			System.out.println("\n\n                     -------- Error occured -------- ");
			System.out.println("                        (!) Invalid arguments (!)");
			System.out.println("                     ------ System terminated ------");
			System.exit(1);
		}
		
		MyHashing mh = new MyHashing(args[0]);
		mh.performTests();
	}
	
	public static void printLayout() {
		System.out.println("--- v1.0 ---------------------------------------------------------------");
		System.out.println("   _     _                       _   _           _     _             \r\n" + 
				"  | |   (_)                     | | | |         | |   (_)            \r\n" + 
				"  | |    _ _ __   ___  __ _ _ __| |_| | __ _ ___| |__  _ _ __   __ _ \r\n" + 
				"  | |   | | '_ \\ / _ \\/ _` | '__|  _  |/ _` / __| '_ \\| | '_ \\ / _` |\r\n" + 
				"  | |___| | | | |  __/ (_| | |  | | | | (_| \\__ \\ | | | | | | | (_| |\r\n" + 
				"  \\_____/_|_| |_|\\___|\\__,_|_|  \\_| |_/\\__,_|___/_| |_|_|_| |_|\\__, |\r\n" + 
				"                                                                __/ |\r\n" + 
				"                                                               |___/\n");
		System.out.println("--- COMP202 | TUC ------------------------------------------ dpetrou ---\n\n");
	}
	
	/**
     * Method that is used to check if a file provided
     * meets some minimum requirements such as:
     * - The file exists
     * - The file is readable
     * - The file path isn't empty or null
     * - The file is a file and not a directory
     * @param fileToCheck File object to be checked
     * @throws FileNotFoundException if at least one of the requirements above isn't met.
     */
	private static void checkFile(String filePath) throws FileNotFoundException {
        
		File fileToCheck = new File(filePath);

		if(fileToCheck.exists() && fileToCheck.canRead() && fileToCheck.isFile() && filePath!=null && !filePath.isEmpty()){
            return;
        }else{
            throw new FileNotFoundException();
        }
    }
	
	
	/**
	 * Method that runs test and records required runtime parameters
	 */
	public void performTests() {	

		System.out.println(" Input Size  LH u>50%   LH u>50%   LH u>50%   LH u>80%   LH u>80%   LH u>80%      BST      BST      BST");
		System.out.println("               avg #      avg #      avg #     avg #      avg #      avg #       avg #    avg #    avg #");
		System.out.println("              insert     search     delete     insert     search     delete     insert   search    delete ");  		
		
		for(int i=0; i<=9900; i=i+100) {
			
			List<Integer> subNbrs = nbrs.subList(i, i+100);
			
			float totalCompIns50T = 0;
			float totalCompSearch50T = 0;
			float totalCompDelete50T = 0;
			
			float totalComparisonsAtInsertion80T = 0;
			float totalComparisonsPerSearch80T = 0;
			float totalComparisonsPerDeletion80T = 0;
			
			float totalCompInsTree = 0;
			float totalCompSearchTree = 0;
			float totalCompDeleteTree = 0;
			
			for(Integer e: subNbrs) {
				myTable50.insertKey(e);
				myTable80.insertKey(e);
				myTree.insertKey(e);
			}
			
			totalCompIns50T+=myTable50.getNumberOfComparisons();
			myTable50.resetComparisons();
			
			totalComparisonsAtInsertion80T+=myTable80.getNumberOfComparisons();
			myTable80.resetComparisons();
			
			totalCompInsTree += myTree.getComparisons();
	       
			
			Random rand = new Random();
			List<Integer> randNbrs = new ArrayList<Integer>();
			
			for (int j=0;j<=50;j++) {
	            int randomIndex = rand.nextInt(nbrs.size()); 
				randNbrs.add(nbrs.get(randomIndex));
			}
			
			for(Integer s : randNbrs) {
				myTable50.searchKey(s);
				myTable80.searchKey(s);
				myTree.searchKey(s);
			}
			
			totalCompSearch50T+=myTable50.getNumberOfComparisons();
			myTable50.resetComparisons();
			
			totalComparisonsPerSearch80T+=myTable80.getNumberOfComparisons();
			myTable80.resetComparisons();
			
			totalCompSearchTree += myTree.getComparisons();
			
			for(Integer s : randNbrs) {
				myTable50.deleteKey(s);
				myTable80.deleteKey(s);
				myTree.delete(s);
			}
			
			totalCompDelete50T+=myTable50.getNumberOfComparisons();
			myTable50.resetComparisons();
			
			totalComparisonsPerDeletion80T+=myTable80.getNumberOfComparisons();
			myTable80.resetComparisons();
			
			totalCompDeleteTree += myTree.getComparisons();
			
			// 1st argument, decimal with 15 character width
			// 2nd argument, float, with 23 character width and 2 decimal point precision
			// 3nd argument, float, with 23 character width and 2 decimal point precision
			System.out.printf("%10d | %7.1f |  %7.1f |  %7.1f |  %7.1f |  %7.1f |  %7.1f | %7.1f | %7.1f | %7.1f  \n", i, totalCompIns50T/100, totalCompSearch50T/50, totalCompDelete50T/50, totalComparisonsAtInsertion80T/100, totalComparisonsPerSearch80T/50, totalComparisonsPerDeletion80T/50, totalCompInsTree/100, totalCompSearchTree/50, totalCompDeleteTree/50);	
		}		
	}
	
}
