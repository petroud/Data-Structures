package mypackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

/**
 * A class bonding the overall functionality of the program
 * by instantiating and running the objects of the Data Structure
 * and other utilities. 
 * @author Dimitris Petrou | 2018030070 | ece@tuc
 *
 */
@SuppressWarnings("all")
public class MyTree {
	
	/**
	 * "A Binary Search Tree implemented with an array" member variable 
	 * used to implement all the operations specified
	 */
	private BST_Arr bst_arr;
	
	/**
	 * "A Binary Search Tree with dynamic memory allocation" member variable
	 * used to implement all the operations specified
	 */
	private BST_Dyn bst_dyn;
	
	/**
	 * A single dimensional array member variable used to store a sorted version
	 * of the numbers provided
	 */
	private int[] array1D;
	
	/**A FileAccess member variable that implements and provided the required methods
	 * for reading the file provided
	 */
	private FileAccess fsys;

	/**
	 * int member variable used to count the comparisons performed while running searches
	 * in the signle dimensional sorted array
	 */
	private int comparisons=0;
	
	
	/**
	 * Class constructor with a String argument
	 * @param filepath : The canonical path of the file to be read
	 */
	public MyTree(String filepath) {
		this.fsys = new FileAccess(filepath);
		
		// The size of the static tree is set according to the number of items
		// contained in the file
		this.bst_arr = new BST_Arr(fsys.getLength());
		this.bst_dyn = new BST_Dyn();
	}
	
	
	/**
    * The main method called from the system that launches the software.
    * @param args The array of String arguments given in the terminal
    * At this program no arguments from the console are used
    */
	public static void main(String[] args) {
		
		/**
		 * A StandardReader object used to read various type of inputs from the console
		 */
		StandardReader reader = new StandardReader();
		
		/**
		 * A String object used to temporally store the path provided from the user
		 */
		String filepath;
		
		/**
		 * A string object used to store at every cycle the command the user gives.
		 */
		String cmd;

		
		//Performing some console print outs
		printOutline();
		printManual();
		
		//Ask the user about the path of the file while an error is occurring 
		while(true) {
			try {
				filepath = reader.readString("--> Specify the path: ");
				checkFile(filepath); //Check if the file meets the minimum requirements
			} catch (FileNotFoundException e) {
				System.out.println("--> File Error | Please try again\n");
				continue;
			}
			System.out.println("--> Success | Application booting...\n");
			break;
		}
		
		System.out.println("---------------- Boot steps -----------------");
		
		System.out.print("  Step 1 | Allocating memory...");
		//Instantiation of the bonding class of the overall functionality
		MyTree mt = new MyTree(filepath);
		System.out.println(" > Completed");
		
		//Call of the constructTree method, that inserts the items of the file
		//in the 3 Data Structures
		mt.constructTree();
		
		System.out.println("---------------------------------------------\n");
					
		//Begin accepting commands 
		while(true) {
			printUserMenu();
			
			//Check if the command the user gave is valid and ask again if
			//an exception is thrown
			try {
				cmd = validateAndReadCommand();
			} catch (Exception e) {
				System.out.println("Bad Command!\n\n");
				continue;
			}
			
			//Switch case to distinguish the different inputs and their correspondent functions
			switch(cmd) {
			
				case "a":
					mt.runTests();
					continue;				
				case "b":
					mt.insert();
					continue;
				case "c":
					mt.search();
					continue;
				case "d":
					mt.inorder();
					System.out.println("\n");
					continue;
				case "e":
					mt.inrange();
					continue;
				case "f":
					System.out.println("Exiting...");
					break;
				default:
					System.out.println("Bad Command!\n\n");
					continue;
			}
			
			break;
		}
	}
	
	
	/**
	 * Method used to test different performance parameters
	 * of the search,insertion and traversal methods for the
	 * 3 data structures
	 * 
	 * There are 4 test cycles performed: 
	 * 	 1) 100 random key searches
	 * 	 2) 100 random in-range searches for a range of 100 items
	 *   3) 100 random in-range searches for a range of 1000 items
	 *   4) N random key searches
	 * 
	 * For every test cycle a repetitive structure is used to run
	 * 100 or N searches, a generator of random numbers provides the 
	 * argument of the search method. 
	 * 
	 * The elapsed time is calculated using the time a method started running
	 * and the time it returned. 
	 * 
	 * The total number of comparisons performed by each method during
	 * every test cycle is summed up and the divided by 100 to find
	 * the mean value. 
	 * 
	 */
	
	private void runTests() {
		
		
		System.out.println("\n-------------------------- Performance Tests ---------------------------");
		System.out.println("--> Performing 100 random key searches...");
		
		int totalComparisonsStatic = 0;
		int totalComparisonsDynamic = 0;
		int totalComparisonsArray = 0;
		
		long totalTimeStatic = 0L;
		long totalTimeDynamic = 0L;
		long totalTimeArray = 0L;

		for(int i=1; i<=100; i++) {			
			int min = -300000000;
			int max = 300000000;
			int randomNum = (int)(Math.random()*((max-min)+1))+min;
			
			long startTime1 = System.nanoTime();
			bst_arr.searchKey(randomNum);
			long endTime1 = System.nanoTime();
			
			totalTimeStatic+=endTime1-startTime1;
			totalComparisonsStatic+=bst_arr.getComparisons();

			long startTime2 = System.nanoTime();
			bst_dyn.searchKey(randomNum);
			long endTime2 = System.nanoTime();
			
			totalTimeDynamic+=endTime2-startTime2;
			totalComparisonsDynamic+=bst_dyn.getComparisons();
			
			this.comparisons = 0;
			long startTime3 = System.nanoTime();
			binarySearchArray(array1D, randomNum, 0, array1D.length-1);
			long endTime3 = System.nanoTime();
			
			totalTimeArray+=endTime3-startTime3;
			totalComparisonsArray+=this.comparisons;
			
		}

		System.out.println("--> Searches Performed");
		System.out.println("--> Average number of comparisons, in the static tree: "+ totalComparisonsStatic/100);
		System.out.println("--> Average number of comparisons, in the dynamic tree: "+ totalComparisonsDynamic/100);
		System.out.println("--> Average number of comparisons, in the Nx1 array: "+ totalComparisonsArray/100);
		System.out.println("--> Total time for 100 random searches, static tree: "+ totalTimeStatic + " ns");
		System.out.println("--> Total time for 100 random searches, dynamic tree: "+ totalTimeDynamic + " ns");
		System.out.println("--> Total time for 100 random searches, Nx1 array: "+ totalTimeArray + " ns");
		
		
		
		System.out.println("\n--> Performing "+fsys.getLength()+" random key searches...");
		
	    totalComparisonsStatic = 0;
	    totalComparisonsDynamic = 0;
		totalComparisonsArray = 0;
		
	    totalTimeStatic = 0L;
		totalTimeDynamic = 0L;
		totalTimeArray = 0L;

		for(int i=1; i<=fsys.getLength(); i++) {
			int min = -300000000;
			int max = 300000000;
			int randomNum = (int)(Math.random()*((max-min)+1))+min;
			
			long startTime1 = System.nanoTime();
			bst_arr.searchKey(randomNum);
			long endTime1 = System.nanoTime();
			
			totalTimeStatic+=endTime1-startTime1;
			totalComparisonsStatic+=bst_arr.getComparisons();

			long startTime2 = System.nanoTime();
			bst_dyn.searchKey(randomNum);
			long endTime2 = System.nanoTime();
			
			totalTimeDynamic+=endTime2-startTime2;
			totalComparisonsDynamic+=bst_dyn.getComparisons();
			
			this.comparisons = 0;
			long startTime3 = System.nanoTime();
			binarySearchArray(array1D, randomNum, 0, array1D.length-1);
			long endTime3 = System.nanoTime();
			
			totalTimeArray+=endTime3-startTime3;
			totalComparisonsArray+=this.comparisons;
			
		}

		System.out.println("--> Searches Performed");
		System.out.println("--> Average number of comparisons, in the static tree: "+ totalComparisonsStatic/fsys.getLength());
		System.out.println("--> Average number of comparisons, in the dynamic tree: "+ totalComparisonsDynamic/fsys.getLength());
		System.out.println("--> Average number of comparisons, in the Nx1 array: "+ totalComparisonsArray/fsys.getLength());
		System.out.println("--> Total time for N random searches, static tree: "+ totalTimeStatic + " ns");
		System.out.println("--> Total time for N random searches, dynamic tree: "+ totalTimeDynamic + " ns");
		System.out.println("--> Total time for N random searches, Nx1 array: "+ totalTimeArray + " ns");


		totalComparisonsStatic=0;
		totalComparisonsDynamic=0;
		totalComparisonsArray=0;
		System.out.println("\n--> Performing 100 random inrange searches (range = 1000)... ");

		for(int i=1; i<=100; i++) {
			int min = -300000000;
			int max = 300000000;
			int randomNum1 = (int)(Math.random()*((max-min)+1))+min;
		
			bst_arr.inrange(randomNum1, randomNum1+1000);
			totalComparisonsStatic+=bst_arr.getComparisons();
			
			bst_dyn.inrange(randomNum1, randomNum1+1000);
			totalComparisonsDynamic+=bst_dyn.getComparisons();

			this.comparisons=0;
			searchArrayInrange(array1D, randomNum1, randomNum1+1000);
			totalComparisonsArray+=this.comparisons;
		}
		System.out.println("--> Searches Performed");
		System.out.println("--> Average number of comparisons, in the static tree (1000 range): "+ totalComparisonsStatic/100);
		System.out.println("--> Average number of comparisons, in the dynamic tree (1000 range): "+ totalComparisonsDynamic/100);
		System.out.println("--> Average number of comparisons, in the Nx1 array (1000 range): "+ totalComparisonsArray/100);

		
		
		
		totalComparisonsStatic=0;
		totalComparisonsDynamic=0;
		totalComparisonsArray=0;
		System.out.println("\n--> Performing 100 random inrange searches (range = 100)... ");

		for(int i=1; i<=100; i++) {
			int min = -300000000;
			int max = 300000000;
			int randomNum1 = (int)(Math.random()*((max-min)+1))+min;
		
			bst_arr.inrange(randomNum1, randomNum1+100);
			totalComparisonsStatic+=bst_arr.getComparisons();
			
			bst_dyn.inrange(randomNum1, randomNum1+100);
			totalComparisonsDynamic+=bst_dyn.getComparisons();
			
			this.comparisons=0;
			searchArrayInrange(array1D, randomNum1, randomNum1+100);
			totalComparisonsArray+=this.comparisons;
		}
		
		System.out.println("--> Searches Performed");		
		System.out.println("--> Average number of comparisons, in the static tree (100 range): "+ totalComparisonsStatic/100);
		System.out.println("--> Average number of comparisons, in the dynamic tree (100 range): "+ totalComparisonsDynamic/100);
		System.out.println("--> Average number of comparisons, in the Nx1 array (100 range): "+ totalComparisonsArray/100);

		System.out.println("-------------------------------------------------------------------------\n");

	}
	
	/**
	 * A method used for the user-case UI random key insertion
	 * 
	 * The user is asked for the desired key to be inserted
	 * using a Scanner object. 
	 * 
	 * Then the key is inserted in the trees using the appropriate 
	 * methods of their classes. The insertion time is calculated
	 * and the results are shown to the user
	 * 
	 */
	private void insert() {
		Scanner in = new Scanner(System.in);		
		
		System.out.println("\n------------------------ Key Insertion --------------------------");		
		System.out.print(" Enter a key to be inserted in the trees: ");		
		int key = (int)in.nextDouble();
		
		System.out.println("--> Inserting key in the static tree...");
		long startTime1 = System.nanoTime();
		bst_arr.insertKey(key);
		long endTime1 = System.nanoTime();
		System.out.println("--> Time elapsed for static tree insertion: " + (endTime1 - startTime1) +" ns");
		System.out.println("--> Number of comparisons performed: " +bst_arr.getComparisons());

		System.out.println("");

		
		System.out.println("--> Inserting key in the dynamic tree...");
		long startTime2 = System.nanoTime();
		bst_dyn.insertKey(key);
		long endTime2 = System.nanoTime();
		System.out.println("--> Time elapsed for dynamic tree insertion: " + (endTime2 - startTime2) +" ns");
		System.out.println("--> Number of comparisons performed: " +bst_dyn.getComparisons());
		
		System.out.println("-----------------------------------------------------------------\n");
	}
	
	/**
	 * A method used for the user-case UI random key search
	 * 
	 * The user is asked for the desired key to be searched
	 * using a Scanner object. 
	 * 
	 * Then a search for the key is performed in the trees using the appropriate 
	 * methods of their classes. The search time is calculated
	 * and the results are shown to the user
	 * 
	 */
	private void search() {
		Scanner in = new Scanner(System.in);	
		System.out.println("\n-------------------------- Key Search ---------------------------");
		System.out.print(" Enter a key to search for it in the trees: ");
		
		int key = (int)in.nextDouble();
		
		System.out.println("--> Searching for the key in the static tree...");
		long startTime1 = System.nanoTime();
		if(bst_arr.searchKey(key)) {
			System.out.println("--> Result: Key found!");
		}else {
			System.out.println("--> Result: Key not found...");
		}
		long endTime1 = System.nanoTime();
		System.out.println("--> Time elapsed for static tree search: " + (endTime1 - startTime1) +" ns");
		System.out.println("--> Number of comparisons performed: " +bst_arr.getComparisons());

		System.out.println("");
		
		System.out.println("--> Searching for the key in the dynamic tree...");
		long startTime2 = System.nanoTime();
		
		if(bst_dyn.searchKey(key)) {
			System.out.println("--> Result: Key found!");
		}else {
			System.out.println("--> Result: Key not found...");
		}
		long endTime2 = System.nanoTime();
		System.out.println("--> Time elapsed for dynamic tree search: " + (endTime2 - startTime2) +" ns");
		System.out.println("--> Number of comparisons performed: " +bst_dyn.getComparisons());
		
		System.out.println("-----------------------------------------------------------------\n");
		
	}
	
	
	/**
	 * A method used for the user-case UI inorder traversal
	 * 
	 * The appropriate methods of the tree classes for traversal are called.
	 * The traversal time for each tree is calculated and the results are 
	 * shown to the user
	 * 
	 */	
	private void inorder() {
		
		System.out.println("\n---------------------- Inorder Traversal ------------------------");

		System.out.println("--> Static Tree inorder traversal running...");
		long startTime2 = System.nanoTime();
		System.out.print("--> Result:   ");
	    bst_arr.inorder();
		long endTime2 = System.nanoTime();
		System.out.println("\n--> Time elapsed for static tree traversal: " + (endTime2 - startTime2) +" ns");
		
		System.out.println("");
	
		System.out.println("--> Dynamic Tree inorder traversal running...");
		System.out.print("--> Result:   ");
		long startTime1 = System.nanoTime();
		bst_dyn.inorder();
		long endTime1 = System.nanoTime();
		
		System.out.println("\n--> Time elapsed for dynamic tree traversal: " + (endTime1 - startTime1) +" ns");
		
		System.out.println("-----------------------------------------------------------------");
	}
	
	/**
	 * A method used for the user-case UI in-range search.
	 * 
	 * The user is asked for the desired range to be searched
	 * using a Scanner object. 
	 * 
	 * The methods for in-range search of each Tree
	 * are called using the range from the user as argument 
	 * 
	 * The search time 
	 */	
	private void inrange() {
		Scanner in = new Scanner(System.in);		
		System.out.println("\n-------------------------- In-Range Search ---------------------------");
		
		System.out.print("Enter lower bound: ");		
		int lower = (int)in.nextDouble();
		
		System.out.print("Enter upper bound: ");		
		int upper = (int)in.nextDouble();

		System.out.println("\n--> Performing in-range search in the static tree...");
		System.out.print("--> Result:    ");
		bst_arr.inrange(lower,upper);
		
		System.out.println("");

		System.out.println("\n--> Performing in-range search in the dynamic tree...");
		System.out.print("--> Result:    ");
		bst_dyn.inrange(lower,upper);
		System.out.println("\n----------------------------------------------------------------------\n");
	}
	
	
	/**
	 * A method used for the construction of the trees
	 * using the keys contained in the file provideed
	 * 
	 * First using the FileSystem object the numbers
	 * are being read from the file and a Vector of Integers containing
	 * them is returned. 
	 * 
	 * For each number in the vector an insertion is made in each tree.
	 * 
	 * For all the insertions an average number of comparisons performed is 
	 * calculated. 
	 * 
	 * The 
	 * 
	 * For the whole process of insertion of all the numbers, the elapsed time
	 * is recorded. 
	 * 
	 * In the end using an inorder traversal through the Static BST we 
	 * construct the single dimensional array.
	 * 
	 * The performance results are shown to the user.
	 * 
	 */	
	private void constructTree() {
		Vector<Integer> listOfNumbers;
		
		System.out.print("  Step 2 | Reading the file....");
		listOfNumbers = fsys.getNumbers();
		System.out.println(" > Completed");

		System.out.println("  Step 3 | Constructing trees.. > Running");
		System.out.println("  Info   | "+ listOfNumbers.size() + " numbers will be used");
		
		System.out.print("  Step 3.1 | Static tree.......");
		int totalComparisonsStatic = 0;
		long startTime1 = System.nanoTime();
		for(Integer e : listOfNumbers) {
			bst_arr.insertKey(e);
			totalComparisonsStatic+=bst_arr.getComparisons();
		}
		long endTime1 = System.nanoTime();
		System.out.println(" > Completed");
		
		
		System.out.print("  Step 3.2 | Dynamic tree......");
		int totalComparisonsDynamic = 0;
		long startTime2 = System.nanoTime();
		for(Integer e: listOfNumbers) {
			bst_dyn.insertKey(e);
			totalComparisonsDynamic+=bst_dyn.getComparisons();
		}
		long endTime2 = System.nanoTime();
		System.out.println(" > Completed");
		
		this.array1D = bst_arr.inorderToArray();
		
        double etSeconds1 = (double) (endTime1-startTime1) / 1_000_000_000;
        double etSeconds2 = (double) (endTime2-startTime2) / 1_000_000_000;      

		
		System.out.println("---------------------------------------------");
		System.out.println("  Static Tree ET : " + (endTime1-startTime1) + " ns | "+ etSeconds1+" s");
		System.out.println("  Dynamic Tree ET: " + (endTime2-startTime2) + " ns | "+ etSeconds2+" s \n");
		
		System.out.println("  Average time per key, Static Tree: " +(endTime1-startTime1)/fsys.getLength() +" ns ");
		System.out.println("  Average time per key, Dynamic Tree: " +(endTime2-startTime2)/fsys.getLength() +" ns ");
	
		System.out.println("  Average number of comparisons, Static Tree: " + totalComparisonsStatic/listOfNumbers.size());
		System.out.println("  Average number of comparisons, Dynamic Tree: " + totalComparisonsDynamic/listOfNumbers.size());
	}
	
	
	/**
	 * Method that searches in binary way in an array within a given range
	 * for a specific key
	 * @param A : the sorted array of numbers
	 * @param value : the key we are searching for
	 * @param low : the upper bound of the search range
	 * @param high : the lower bound of the search range
	 * @return int : the pos in which the search will begin again recursively
	 * 				 or the pos of the key if it was finally found.
	 * @from GeeksForGeeks
	 */
	private int binarySearchArray(int[] A , int value, int low, int high) {
		
		if (high < low)  {
			this.comparisons++;
	        return -1; // not found  
		}
		
	    int mid = low + ((high - low) / 2);
	    
	    if (A[mid] > value) {
	    	this.comparisons++;
	        return binarySearchArray(A, value, low, mid-1);
	    }
	    else if (A[mid] <value) {
	    	this.comparisons++;
	        return binarySearchArray(A, value, mid+1, high);
	    }
	    else
	       return mid; // found
	}
	
	
	/**
	 * A method that searches a sorted array for a given range
	 * It uses the searchLeft and the searchRight to search both 
	 * middles of the array (iteratively from the middle to the edges)
	 * @param arr : the sorted array
	 * @param start : the lower bound of the range
	 * @param end : the upper bound of the range
	 * @return : not used
	 * @from StackOverflow
	 */
	private int searchArrayInrange(int[] arr, int start, int end) {
	    int leftIndex = searchLeft(arr, start);
	    int rightIndex = searchRight(arr, end);

	    if (leftIndex < 0 || rightIndex < 0) {
	    	this.comparisons+=2;
	        return -1;
		}
	    if (rightIndex == leftIndex) {
	    	this.comparisons++;
	    }
	    else {
	        return 0;
	    }
	    
	    return 0;
	}

	/**
	 * Method that searches a sorted array from the middle to the left edge
	 * in a binary way using positioning transitions
	 * @param arr : the sorted array
	 * @param start : the start index
	 * @return : not used
	 * @from StackOverflow
	 */
	private int searchLeft(int[] arr, int start) {
	    int lo = 0;
	    int hi = arr.length - 1;

	    while (lo <= hi) {
	    	this.comparisons++;
	        int mid = lo + (hi - lo) / 2;

	        if (arr[mid] == start && arr[mid -1] < start) {
	            this.comparisons+=2;
	        	return mid - 1;
	        }

	        if (arr[mid] >= start) {
	        	this.comparisons++;
	            hi = mid - 1;
	        }
	        else
	            lo = mid + 1;
	    }

	    return -1;
	}

	/**
	 * Method that searches a sorted array from the middle to the right edge
	 * in a binary way using positioning transitions
	 * @param arr : the sorted array
	 * @param end : the end index
	 * @return : not used
	 * @from StackOverflow
	 */
	private int searchRight(int[] arr, int end) {
	    int lo = 0;
	    int hi = arr.length -1;

	    while (lo <= hi) {
	    	this.comparisons++;
	        int mid = lo + (hi - lo) / 2;

	        if (arr[mid] == end && arr[mid+1] > end) {
	            this.comparisons+=2;
	        	return mid;
	        }
	        if (mid <= end) {
	        	this.comparisons++;
	            lo = mid + 1;
	        }
	        else
	            hi = mid - 1;
	    }

	    return -1;
	}

	/**
     * Method that is used to check if a file provided
     * meets some minimum requirements such as:
     * - The file exists
     * - The file is readable
     * - The file is writable
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
     * Method that is used to read a String command from the console
     * and return it.
     * @return String : the command read from the console
     */
	private static String readCommand(){
		
		StandardReader reader = new StandardReader();
        String readStr = reader.readString("Operation: ");
        return readStr;
        
    }
	
	 /**
     * Method that checks if a command is:
     * - 1 character long
     * - not empty
     * - not null
     * @return The command that was read if it's valid
     * @throws Exception if at least one of the requirements above is not met
     */
	public static String validateAndReadCommand() throws Exception {
       
		String cmd = readCommand();
       
        if(cmd.length()>1 || cmd.isEmpty() || cmd == null){
                throw new Exception();
        }
        
        return cmd;
    }


	/**
     * Method for printing the user menu
     */
	private static void printUserMenu() {
		
		System.out.println("----------- User Menu ----------");
		System.out.println(" a) Execute performance tests");
		System.out.println(" b) Insert random key");
		System.out.println(" c) Search random key");
		System.out.println(" d) Inorder traversal");
		System.out.println(" e) Range Search");
		System.out.println(" f) Exit");
		System.out.println("--------------------------------");
		
	}
	
	/**
     * Method for printing some basic info and instructions about the file to the user
     */
	private static void printManual() {
		
		System.out.println("\n--> Welcome to the BST application, constructing a static and a dynamic B.S.T. from a file !");
		System.out.println("--> You will be asked to provide a path of a file, which contains integer numbers");
		System.out.println("--> The file must be: ");
		System.out.println("     a) Existent");
		System.out.println("     b) A file");
		System.out.println("     c) Readable");
		System.out.println("     d) In Big Endian format");
		System.out.println("     e) Binary\n");
		
	}
	
	/**
     * Method for printing a simple text art logo in the console
     */
	private static void printOutline() {
		System.out.println("--- v1.0 -------------------------------");
		System.out.println("      ____        _____    _______ \r\n" + 
				"     |  _ \\      / ____|  |__   __|\r\n" + 
				"     | |_) |    | (___       | |   \r\n" + 
				"     |  _ <      \\___ \\      | |   \r\n" + 
				"     | |_) |     ____) |     | |   \r\n" + 
				"     |____/ (_) |_____/ (_)  |_| (_)  \r\n" + 
				"                         ");
		System.out.println("--- COMP202 | TUC ---------- dpetrou ---");
	}
	
	
}