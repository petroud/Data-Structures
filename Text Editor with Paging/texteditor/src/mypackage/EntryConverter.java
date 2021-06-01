package mypackage;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class used for converting byte arrays to lists of WordEntry objects
 * and the opposite
 */
public class EntryConverter {

    /**
     * Class constructor
     */
    public EntryConverter(){

    }

    /**
     * Member variable for the MAX_WORD_SIZE value
     * It follows the Indexer's member variable by using
     * a static method provided.
     */
    private static int MAX_WORD_SIZE = Indexer.getMaxWordSize();

    /**
     * Member variable for the PAGE_SIZE value
     * It follows the Indexer's member variable by using
     * a static method provided.
     */
    private static int PAGE_SIZE = Indexer.getPageSize();

    /**
     * Member variable for the BUFFER_SIZE value
     * It is set according to the given MAX_WORD_SIZE variable
     */
    private static int BUFFER_SIZE = MAX_WORD_SIZE + 4;


    /**
     * Method for creating a list of WordEntry objects
     * to an array of bytes
     * The list usually contains as much items as a page can fit
     * @param entries : The list of WordEntry objects to be converted
     * @return byte[] : The byte array corresponding to the entries
     */
    public byte[] listToBytes(List<WordEntry> entries){

        ByteBuffer bb = ByteBuffer.allocate(PAGE_SIZE);  //Creating a ByteBuffer object to fit all the items of the list

        //Iterating the list and converting each item to byte array and then appending it in the byte buffer
        for(WordEntry w: entries) {
            String curText = w.getText();  //Getting the word of the current entry
            int curInt = w.getLineNumber(); //Getting the lineNumber of the current entry

            curText = fixedLengthString(curText);  //Adjusting the word size to the MAX_WORD_SIZE by adding spaces
            bb.put(curText.getBytes(StandardCharsets.US_ASCII)); //Converting the word to bytes and storing it
            bb.putInt(curInt); //Storing the int lineNumber
        }

        byte[] byteArray = bb.array();  //Converting the ByteBuffer containing all the entries to byte array

        return byteArray;  // Returning the produced byte array
    }

    /**
     * Method that converts a byte array read from a file page
     * to a list of WordEntry items
     * @param data The page byte array that was read from the FilePageAccess.read()
     * @return List<WordEntry> The list of word entries described from the bytes provided
     */
    public List<WordEntry> bytesToList(byte[] data){

        //The list to be returned
        List<WordEntry> pageEntries = new ArrayList<WordEntry>();

        //An array for storing the bytes of each entry
        byte[] entryBytes;

        ByteBuffer wordBuffer;

        int entriesPerPage = PAGE_SIZE/BUFFER_SIZE;

        //The size of the offset that shows us where the integer is appended in a byte array of one word entry
        int offsetNbr = MAX_WORD_SIZE;

        //The size of the offset that shows us the size of the word in a byte array of one word entry
        int offsetWrd = 0;

        //We are gonna read as many entries as a page can fit
        for(int i=0; i<entriesPerPage;i++){

            //Getting the lineNumber of the current word entry
            int lineNum = ByteBuffer.wrap(data, offsetNbr,4).getInt(offsetNbr);

            //Cutting the entry's text corresponding bytes and putting it into a temporal byte array
            entryBytes = Arrays.copyOfRange(data, offsetWrd,offsetWrd+MAX_WORD_SIZE);

            //Wrapping the word
            wordBuffer = ByteBuffer.wrap(entryBytes);

            //Converting the word from the wrapped byte array to String using a charset standard.
            String textWord = new String(wordBuffer.array(), StandardCharsets.US_ASCII);

            //When we find null bytes the operation ends because no more entries are contained in the page
            if(textWord.isEmpty() || textWord==null || lineNum == 0){
                break;
            }

            //Adding the converted entry in the list to be returned
            pageEntries.add(new WordEntry(textWord,lineNum));

            //Increasing the offsets for the line number and the text so that we can move and convert the next entry
            offsetWrd+=BUFFER_SIZE;
            offsetNbr+=BUFFER_SIZE;
        }


        return pageEntries;
    }


    /**
     * Method for adjusting the length of string to MAX_WORD_SIZE by adding spaces if neededs
     * @param string : The word to be adjusted
     * @return String : The adjusted String
     */
    public String fixedLengthString(String string) {
        return String.format("%1$"+MAX_WORD_SIZE+ "s", string);
    }
}



