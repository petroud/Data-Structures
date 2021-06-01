package mypackage;

/**
 * A class that corresponds to a Hash bucket of the Hash table,
 * containing a certain amount of keys 
 */
public class HashBucket {
	
	/**
	 * The overflow bucket of the parent bucket, used for separate chaining method
	 */
	private HashBucket overflowBucket;
	
	/**
	 * An array of integers containing the keys of the bucket
	 */
	private int[] keys;
	
	/**
	 * The number of total keys that are currently contained in this parent bucket
	 */
	private int numberOfKeys;
	
	
	/**
	 * Constructor with an integer argument 	
	 * @param bs : the max number of keys to be inserted in the bucket
	 */
	public HashBucket(int bs) {
		this.keys = new int[bs];
		this.overflowBucket = null;
		this.numberOfKeys = 0;
	}
	
	
	/**
	 * Getter for the member variable counting the number of keys that currently exist in the bucket
	 * @return int : numberOfKeys
	 */
	public int getNumberOfKeys() {
		return this.numberOfKeys;
	}
	
	
	/**
	 * Method that inserts a key in the array of integers of the bucket
	 * or of the overflow buckets.
	 * It first checks if the key already exists in the Bucket. If there is
	 * a similar key then the method returns.
	 * If not, the key is inserted at the next available position(based on i) 
	 * if the parent bucket is not full. Else the key is inserted in one of 
	 * the overflow buckets recursively (if no overflow buckets are created, a new 
	 * one is being instantiated).
	 * @param key :  The key to be inserted
	 * @param lh : The linear hashing of the problem instance for parameter updates.
	 * 
	 */
	public void insertKey(int key, LinearHashing lh) {
		
		int bucketSize = lh.getBucketSize();
		int keysNum = lh.getTotalNumberOfKeys();
		int keySpace = lh.getMaxKeys();
		int i;
		
		for(i=0; i<this.numberOfKeys && i<bucketSize ; i++) {
			lh.setNumberOfComparisons(lh.getNumberOfComparisons()+1);
			if(this.keys[i] == key) {
				return;
			}
		}
		
		lh.setNumberOfComparisons(lh.getNumberOfComparisons()+1);
		if(i<bucketSize) {
			this.keys[i] = key;
			this.numberOfKeys++;
			lh.setTotalNumberOfKeys(keysNum++);
		}else {
			
			lh.setNumberOfComparisons(lh.getNumberOfComparisons()+1);
			if(this.overflowBucket != null) {
				this.overflowBucket.insertKey(key, lh);
			}else {
				this.overflowBucket = new HashBucket(bucketSize);
				lh.setMaxKeys(keySpace+=bucketSize);
				this.overflowBucket.insertKey(keySpace, lh);
			}
		}
		
	}
	
	/**
	 * Method that removes the last key from the bucket and overflow buckets. 
	 * If the parent bucket has no overflow buckets attached then simply
	 * the last key is removed and the new last key is returned. If the parent 
	 * bucket has overflow buckets then successively move to the last overflow
	 * bucket and remove the last key of it following tha same logic.  
	 * 
	 * @param lh : the linear hashing class used for parameter updates
	 * @return the new last key of the Hash Bucket
	 */
	public int removeLastKey(LinearHashing lh) {	// remove bucket last key

		int retval;
		int bucketSize = lh.getBucketSize();
		int keySpace = lh.getMaxKeys();

		
		if (this.overflowBucket == null) {
		  if (this.getNumberOfKeys() != 0){
		    this.numberOfKeys--;
		    return this.keys[this.numberOfKeys];
		  }
		  return 0;
		}
		else {
		  retval = this.overflowBucket.removeLastKey(lh);
		  if (this.overflowBucket.getNumberOfKeys() == 0) {	// overflow empty free it
		    this.overflowBucket = null;
		    keySpace -= bucketSize;
		    lh.setMaxKeys(keySpace);			// update linear hashing class.
		  }
		  return retval;
		}
	}
	
	/**
	 * The method searches for the key among the keys of the parent bucket
	 * while avoiding index out of bounds errors.
	 * 
	 * If the method does not find anything corresponding to our key argument
	 * in the parent bucket, it moves on to the overflow buckets and performs
	 * a search there recursively. 
	 * 
	 * @param key : the key we are searching for 
 	 * @param lh :the linear hashing class for parameter updates
	 * @return true/false found or not
	 *
	 */
	public boolean searchKey(int key, LinearHashing lh) {

		for (int i = 0; (i < this.numberOfKeys) && (i < lh.getBucketSize()); i++) {
		   lh.setNumberOfComparisons(lh.getNumberOfComparisons()+1);
		   if (this.keys[i] == key) {	//key found
		     return true;
		   }
		}
		
		if (this.overflowBucket != null) {				
		  return this.overflowBucket.searchKey(key,lh);
	    }else {
		  return false;
	    }
		
	}
	

	/**
	 * Method that prints the contents of bucket
	 * @param bucketSize : The size of a bucket, used to know where to stop printing
	 */
    public void printBucket(int bucketSize) {

		System.out.println("Number of keys in here:" + this.getNumberOfKeys());
		
	    for (int i = 0; (i < this.getNumberOfKeys()) && (i < bucketSize); i++) {
		   System.out.println("Key at: " + i + " is: " + this.keys[i]);
		}
		
		if (this.overflowBucket != null) {
		  System.out.println("Printing overflow...");
		  this.overflowBucket.printBucket(bucketSize);
		}
		
		System.out.println("----------------------------------------\n");
		
	}
    
    /**
     * Method that deletes a key from a bucket taking into account its index
     * 
     * @param key: The key to be deleted
     * @param lh: The linear hashing class used for parameter updates
     */
    public void deleteKey(int key, LinearHashing lh) { // code not correct

		int i;
		int bucketSize = lh.getBucketSize();
		int keysNum = lh.getTotalNumberOfKeys();
		int keySpace = lh.getMaxKeys();

		for (i = 0; (i < this.numberOfKeys) && (i < bucketSize); i++) {
		   lh.setNumberOfComparisons(lh.getNumberOfComparisons()+1);
		   if (this.keys[i] == key) {
		     if (this.overflowBucket == null) {		// no overflow
				 this.keys[i] = this.keys[this.numberOfKeys-1];
				 this.numberOfKeys--;
				 keysNum--;
				 lh.setTotalNumberOfKeys(keysNum);			// update linear hashing class.
		     }
		     else {	// bucket has an overflow so remove a key from there and bring it here
				 this.keys[i] = this.overflowBucket.removeLastKey(lh);
				 keysNum--;
				 lh.setTotalNumberOfKeys(keysNum);			       // update linear hashing class.
				 if (this.overflowBucket.getNumberOfKeys() == 0) { // overflow empty free it
					 this.overflowBucket = null;
					 keySpace -= bucketSize;
					 lh.setMaxKeys(keySpace);			// update linear hashing class.
				 }
		     }
		     return;
		   }
		}
		if (this.overflowBucket != null) {			// look at the overflow for the key to be deleted if one exists
			this.overflowBucket.deleteKey(key, lh);
			if (this.overflowBucket.getNumberOfKeys() == 0) {	// overflow empty free it
				this.overflowBucket = null;
				keySpace -= bucketSize;
				lh.setMaxKeys(keySpace);				// update linear hashing class.
			}
	    }
	}
    
    /**
     * 
     * @param lh
     * @param n
     * @param bucketPos
     * @param newBucket
     */
    public void splitBucket(LinearHashing lh, int n, int bucketPos, HashBucket newBucket) {	//splits the current bucket

		int i;
		int bucketSize = lh.getBucketSize();
		int keySpace = lh.getMaxKeys();
		int keysNum = lh.getTotalNumberOfKeys();

		for (i = 0; (i < this.numberOfKeys) && (i < bucketSize);) {
			
		   if ((this.keys[i]%n) != bucketPos){	//key goes to new bucket
		       newBucket.insertKey(this.keys[i], lh);
		       this.numberOfKeys--;
		       keysNum = lh.getTotalNumberOfKeys();
		       keysNum--;
		       lh.setTotalNumberOfKeys(keysNum);		// update linear hashing class.		       //System.out.println("HashBucket.splitBucket.insertKey: KeysNum = " + keysNum );
		       this.keys[i] = this.keys[this.numberOfKeys];
		   }else {				// key stays here
		     i++;
		   }
		   
		}

		if (this.overflowBucket != null) {	// split the overflow too if one exists
		  this.overflowBucket.splitBucket(lh, n, bucketPos, newBucket);
		}
		
		while (this.numberOfKeys != bucketSize) {
		     
			 if (this.overflowBucket == null) {
				 return;
		     }
		     
		     if (this.overflowBucket.getNumberOfKeys() != 0) {
		    	 
		         this.keys[this.numberOfKeys] = this.overflowBucket.removeLastKey(lh);
		         
		         if (this.overflowBucket.getNumberOfKeys() == 0) {	// overflow empty free it
		    	    this.overflowBucket = null;
		    	    keySpace -= bucketSize;
		    	    lh.setMaxKeys(keySpace);      // update linear hashing class.
		         }
		         
		         this.numberOfKeys++;
		         
		     }else {				// overflow empty free it
				 this.overflowBucket = null;
				 keySpace -= bucketSize;
			     lh.setMaxKeys(keySpace);	// update linear hashing class.
		     }
	 	}
		
	}
	
	/**
	 * Method that merges the current bucket with the bucket given as oldBucket argument
	 * While the oldBucket has keys left, they are inserted in this bucket.
	 * 
	 * @param lh : the linear hashing class for parameter updates
	 * @param oldBucket the bucket to be merged with an other one
	 */
	public void mergeBucket(LinearHashing lh, HashBucket oldBucket) {	//merges the current bucket
		while (oldBucket.getNumberOfKeys() != 0) {
		     this.insertKey(oldBucket.removeLastKey(lh), lh);
		}
	}
    
    
    

}
