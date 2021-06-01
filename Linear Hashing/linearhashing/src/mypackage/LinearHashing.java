package mypackage;

@SuppressWarnings("all")
/**
 * The wrapper class of the linear hashing program. It keeps 
 * the tables with the hash buckets and it gives access
 * to the methods of the them, 
 * @author dpetrou
 *
 */
public class LinearHashing {
	
	/**
	 * Maximum load factor of a bucket
	 */
	private float maxFactor;
	
	/**
	 * Minimum load factor of a bucket
	 */
	private float minFactor;
	
	/**
	 * Key value used for calculations in the hash function
	 */
	private int hashKey;
	
	/**
	 * The max number of key in each parent bucket created
	 */
	private int bucketSize;
	
	/**
	 * The total number of keys contained in the buckets of the hash table
	 */
	private int totalNumberOfKeys;
	
	/**
	 * The total number of buckets in the hash table
	 */
	private int totalNumberOfBuckets;
	
	/**
	 * The total space for keys
	 */
	private int maxKeys;
	
	/**
	 * Pointer to the next bucket to be split (index in the table)
	 */
	private int nextToSplit;
	
	/**
	 * The minimum number of buckets that can be contained in the hash table
	 */
	private int minBuckets;
	
	/**
	 * The hash table with the hash buckets
	 */
	private HashBucket[] hashBuckets;
	
	/**
	 * Member variable for counting the number of comparisons performed after each action
	 */
	private int numberOfComparisons;
	

	/**
	 * Class constructor with 2 integer arguments
	 * @param bs : the number of keys that fit in its bucket
	 * @param bckts : the number of buckets of the hash table
	 */
	public LinearHashing(int bs, int bckts) {
		
		this.bucketSize = bs;
		this.totalNumberOfBuckets = bckts;
		
		if(bucketSize==0 || totalNumberOfBuckets == 0) {
			System.out.println("-->System Warning! : The hash table can not contain 0 buckets or a bucket can not contain 0 keys");
			System.exit(1);
		}
		
		this.hashBuckets = new HashBucket[totalNumberOfBuckets];
		initializeBuckets();

		this.maxFactor = 0.8f;
		this.minFactor = 0.5f;
		
		this.minBuckets = totalNumberOfBuckets;
		this.hashKey = bckts;
		this.maxKeys = bs*bckts;
		this.numberOfComparisons = 0;
	}
	
	/**
	 * Method to instantiate each bucket of the table to 
	 * the set size
	 */
	private void initializeBuckets() {
		for(int i=0; i<=totalNumberOfBuckets-1; i++) {
			hashBuckets[i] = new HashBucket(bucketSize);
		}
	}
	
	/**
	 * Method that returns the load factor of the table
	 * by dividing the number of keys with the table's capacity
	 * @return float : decimal load factor of the table
	 */
	public float getLoadFactor() {
		return (float)(this.totalNumberOfKeys/this.maxKeys);
	}
	
	
	public int getBucketSize() {
		return bucketSize;
	}

	public void setBucketSize(int bucketSize) {
		this.bucketSize = bucketSize;
	}

	public int getTotalNumberOfKeys() {
		return totalNumberOfKeys;
	}

	public void setTotalNumberOfKeys(int totalNumberOfKeys) {
		this.totalNumberOfKeys = totalNumberOfKeys;
	}
	

	public int getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(int maxKeys) {
		this.maxKeys = maxKeys;
	}
	
	public void setLoadFactor(float f) {
		this.maxFactor = f;
	}
	
	

	public int getNumberOfComparisons() {
		return numberOfComparisons;
	}

	public void setNumberOfComparisons(int numberOfComparisons) {
		this.numberOfComparisons = numberOfComparisons;
	}
	
	public void resetComparisons() {
		setNumberOfComparisons(0);
	}

	/**
	 * The hash function of the table, connecting a hash position with
	 * a random key
	 * 
	 * First we try generating a hash index using only the mod based on the key.
	 * If the result is negative we convert it to positive and we continue.
	 * If the index produced is greater than the index of the of the next bucket to be split then we return it
	 * Else we perform rehash with negative result check. 
	 * @param key : the key to be related
	 */
	private int hash(int key) {
		int idx;
		
		idx = key % this.hashKey;
		
		if(idx < 0) {
			idx*=-1;
		}
		
		if(idx >= nextToSplit) {
			return idx;
		}else {
			idx = key % (2 * this.hashKey);
			if (idx < 0) {
		   	   idx *= -1;
		    }
		    return idx;
		}
		
	}
	
	/**
	 * Method that hashes a bucket for a given key
	 * and inserts it in the bucket (the bucket handles the insertion later)
	 * 
	 * If the load factor of the table is greater than the max factor set before
	 * then the current bucket is split.
	 * @param key : the key to be inserted in the table
	 */
	public void insertKey(int key) {
		this.hashBuckets[this.hash(key)].insertKey(key, this);
		if (this.getLoadFactor() > maxFactor){
		  this.bucketSplit();
		}
	}
		
	/**
	 * Method that searches for a key in the table.
	 * It hashes a bucket position based on the key and it searches
	 * if the bucket contains the key using the provided method 
	 * @param key : the key to search
	 * @return boolean :  true/false if the key was found or not
	 */
	public boolean searchKey(int key) {
		return this.hashBuckets[this.hash(key)].searchKey(key, this);
	}
	
	/**
	 * Method that deletes a key from the table.
	 * It hashes a bucket position based on the key and it uses
	 * the bucket's method to delete the key if it's found.
	 * 
	 * Then it checks the load factor of the table and it merges 
	 * or splits it. If the table has an invalid number of buckets 
	 * then the method returns without doing anything for merging or spliting.
	 * @param key : The key to be deleted
	 */
	public void deleteKey(int key) {
		this.hashBuckets[this.hash(key)].deleteKey(key, this);
		
		if (this.getLoadFactor() > maxFactor){
		  this.bucketSplit();
		}
		else if ((this.getLoadFactor() < minFactor) && (this.totalNumberOfBuckets > this.minBuckets)){
			 this.bucketMerge();
		}
	}
	
	/**
	 * Method that prints the whole hash table and its buckets
	 */
	public void printHash() {
		for (int i = 0; i < this.totalNumberOfBuckets; i++) {
		   System.out.println("Bucket[" + i + "]");
		   this.hashBuckets[i].printBucket(this.bucketSize);
		}
	}
	
	/**
	 * Method used to split the bucket at the position 
	 * pointed by the nextToSplit member variable. 
	 * 
	 * It generates a new table of buckets (as many as there currently are)
	 * and it instantiates them by assigning to them the old ones.
	 * It creates n+1 new buckets.
	 * Then the new table of buckets becomes the table of this class.
	 * The 1 extra bucket created is instantiated.
	 * The variables that are related to table properties are adjusted.
	 * 
	 * The bucket that is next to split is split and its contents 
	 * are moved to 1 extra bucket created.
	 * 
	 * The hashKey is adjusted appropriately in order to match
	 * the new properties of the table. If the hash keys left 
	 * are none then the hashKey doubles and the the next bucket to be split
	 * is set to be the first one. Else the next bucket to be split is set
	 * to be the next one.
	 * 
	 */
	private void bucketSplit() {

		HashBucket[] newHashBuckets;
		newHashBuckets= new HashBucket[this.totalNumberOfBuckets+1];
		
		for (int i = 0; i < this.totalNumberOfBuckets; i++){
		   newHashBuckets[i] = this.hashBuckets[i];
		}

		hashBuckets = newHashBuckets;
		hashBuckets[this.totalNumberOfBuckets] = new HashBucket(this.bucketSize);
		this.maxKeys += this.bucketSize;
		this.hashBuckets[this.nextToSplit].splitBucket(this, 2*this.hashKey, this.nextToSplit, hashBuckets[this.totalNumberOfBuckets]);
		this.totalNumberOfBuckets++;
		
		if (this.totalNumberOfBuckets == 2*this.hashKey) {
		  this.hashKey = 2*this.hashKey;
		  this.nextToSplit = 0;
		}
		else {
		    this.nextToSplit++;
		}
	}
	
	/**
	 * Method used to merge the last bucket that was split last
	 * The method has the opposite logic of the split method.
	 * 
	 * It generates a new table of buckets with n-1 elements
	 * It instantiates each element one by one by assigning to them
	 * the ones from the old table.
	 * 
	 * The variables that are related to table properties are adjusted.
	 * 
	 * The last bucket that was split is being merged with the last bucket of the new array 
	 * The new table becomes the hash table of this class.
	 * 
	 * The hashKey is adjusted appropriately in order to match
	 * the new properties of the table. If the hash keys left 
	 * are none then the hashKey doubles and the the next bucket to be split
	 * is set to be the first one. Else the next bucket to be split is set
	 * to be the previous one.
	 */
	private void bucketMerge() { 	

		HashBucket[] newHashBuckets;
		newHashBuckets= new HashBucket[this.totalNumberOfBuckets-1];
		
		for (int i = 0; i < this.totalNumberOfBuckets-1; i++) {
		   newHashBuckets[i] = this.hashBuckets[i];
		}
		
		if (this.nextToSplit == 0) {
		  this.hashKey = (this.totalNumberOfBuckets)/2;
		  this.nextToSplit = this.hashKey-1;
		}else {
		  this.nextToSplit--;
		}
		
		this.totalNumberOfBuckets--;
		this.maxKeys -= this.bucketSize;
		this.hashBuckets[this.nextToSplit].mergeBucket(this, hashBuckets[this.totalNumberOfBuckets]);
		hashBuckets = newHashBuckets;
	}	

}
