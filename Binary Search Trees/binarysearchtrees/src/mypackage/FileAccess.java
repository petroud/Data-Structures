package mypackage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Class that provides methods for reading the numbers
 * from the provide file using RandomAccessFile logic
 * 
 * @author Dimitris Petrou | 2018030070 | ece@tuc
 * 
 */
public class FileAccess {
	
	/**
	 * A String member variable used to store the path to the file
	 */
	private String filepath;
	
	/**
	 * A vector of integers used to store the numbers after they are read from the file
	 */
	private Vector<Integer> numbers;
	
	/**
	 * A file object corresponding to the file of the path provided
	 */
	private File inputFile;
	
	/**
	 * A byte array used to store each byte set read from the file
	 */
	private byte[] inputData = null;

	/**
	 * Static variable for setting a constant for the size of an integer in bytes
	 */
	private final int intSize = 4;
	
	/**
	 * Variable used to store the size of the file in the bytes 
	 */
	private long fileSize = 0;
	
	/**
	 * Variable to store the number of numbers in the file, after calculating it
	 */
	private long numbersInFile = 0;
	
	
	/**
	 * Class constructor with String argument
	 * @param filepath The path to the file containing the numbers
	 */
	public FileAccess(String filepath) {
		this.filepath = filepath;
		setup();
	}
	
	/**
	 * Method instantiating, initializing and setting the member variables of the class
	 */
	public void setup() {
		this.numbers = new Vector<Integer>();
		this.inputFile = new File(filepath);
		this.fileSize = inputFile.length();
		this.numbersInFile = fileSize/intSize;
	}
	
	public Vector<Integer> getNumbers(){
		readNumbersFromFile();
		return numbers;
	}
	
	public int getLength() {
		return (int)this.numbersInFile;
	}
	
	/**
	 * Method that uses a RandomAccessFile object to read iteratively 
	 * the numbers from the file and store them in a vector.
	 * 
	 * The method read 4 by 4 the bytes from the file for as many times as 
	 * the numbers contained are (calculated by the size).
	 * 
	 * It converts them to integers and stores each number in the vector
	 */
	public void readNumbersFromFile() {

		try {
			RandomAccessFile raf = new RandomAccessFile(inputFile,"rwd");
			ByteBuffer bb;

			inputData = new byte[4];
			
			for(int i = 0; i<=numbersInFile-1; i++) {
				raf.seek(i*intSize);
				raf.read(inputData);
				
				bb = ByteBuffer.wrap(inputData);
				numbers.add(bb.getInt());
			}
			
			raf.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
