package mypackage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class for writing and reading from a file
 * using the RandomAccessFile principles
 * It also provides methods for checking different
 * characteristics of a file at a specific point
 */
@SuppressWarnings("ALL")

public class FilePageAccess {

    /**
     * Member variable for storing the path of the file
     */
    private String filepath;

    /**
     * Member variable for storing the size of a disk virtual
     * page. It follows the member variable of the Indexer
     * by using a static method provided.
     */
    private int pageSize = Indexer.getPageSize();

    /**
     * An int member variable for keeping track of the current
     * position in the file, for updating the seek() method
     * accordingly.
     */
    private int currentPage;


    /**
     * Class constructor with a string argument
     * @param fpth The canonical path of the indexing file to be created.
     */
    public FilePageAccess(String fpth){
        this.filepath = fpth;
        this.currentPage = -1; //Initializing the currentPage int variable.
    }


    /**
     * Method that reads a specific range inside the indexing file
     * The range that is to be read depends on the page_size
     * and the begin point depends on the int argument 'pageNumber'
     * @param pageNumber : The point from which we start reading (translated later as page)
     * @return byte[] : A byte array that corresponds to the desired page.
     */
    public byte[] readPage(int pageNumber){

        //Allocating a byte array. It's size sets the read limit from the file
        byte[] data = new byte[this.pageSize];

        try {
            //Instatiation of a RandomAccessFile object, to provides file read features.
            RandomAccessFile raf = new RandomAccessFile(filepath,"r");
            //Placing the pointer to the begin index of a page according to the number of page given as an arg.
            raf.seek(this.pageSize*pageNumber);
            //The RandomAccessFile object reads as much bytes as the data[] size is, and places them in it.
            raf.read(data);
            //Closing the RandomAccessFile object
            raf.close();

            //Keeping track of on which page we are.
            this.currentPage = pageNumber;

            //Return the completed byte array that corresponds to the pageNumber given.
            return data;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Method that writes an array of bytes at a specific position of the indexing file
     * @param pagePos : The number of the page in which the bytes are going to be written
     * @param data : The array of bytes to be written in the file
     */
    public void write(int pagePos, byte[] data){

        RandomAccessFile raf = null;
        try {
            //Instatiation of a RandomAccessFile object to provide file write features
            //Giving the filepath as an argument to the RandomAccessFile constructor
            raf = new RandomAccessFile(filepath,"rwd");
            //Placing the pointer at the beginning index of the page according to the pagePos
            raf.seek(this.pageSize*pagePos);
            //Writing the byte array in the file
            raf.write(data);
            //Closing the RandomAccessFile object
            raf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
