package mypackage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Indexer class is used for indexing the file and cutting it
 * to words. Statistics can be exported through this class
 * and searches can be done.
 */
public class Indexer {

    /**
     * Member variable for storing the list of lines
     * coming from the commander
     */
    private LinkedList<FileLine> fileLines;

    /**
     * Member variable for storing the word entries of a line
     * while iterating through the list of lines
     */
    private List<String> lineWords;

    /**
     * Member variable for storing a vector containing
     * all the words of the file and the number in which
     * each of them was found
     */
    private Vector<WordEntry> fileWords;

    /**
     * A reader object used for reading console input
     */
    private StandardReader reader;

    /**
     * The path in which the indexing file will be created
     */
    private String indexingPath;

    /**
     * A boolean variable for keeping track of when the words
     * of the file have been sorted.
     */
    private boolean sorted = false;

    /**
     * A boolean variable for keeping track of when the indexing
     * file has been created.
     */
    private boolean indexingCreated = false;

    /**
     * Static member variable that sets the minimum size
     * of a word in order to be taken into account
     * when sorting the file and spliting it to words.
     */
    private static final int MIN_WORD_SIZE = 5;

    /**
     * Static member variable that sets the maximum size
     * of a word to be sorted. If the word length exceeds
     * this number, it is being adjusted to it.
     */
    private static final int MAX_WORD_SIZE = 20;

    /**
     * Member variable which determines the size
     * of a disk virtual page size.
     */
    private static final int PAGE_SIZE = 128;

    /**
     * Member variable which determines the size
     * of the byte array of a word entry.
     */
    private static final int BUFFER_SIZE = MAX_WORD_SIZE + 4;


    /**
     * Class constructor with LinkedList<> argument
     * @param list The list of FileLine items containing the lines of the file which is going to be indexeds
     */
    public Indexer(LinkedList<FileLine> list){
        this.fileLines = list;
        fileWords = new Vector<WordEntry>();
        reader = new StandardReader();
    }

    /**
     * Static public method returning the MAX_WORD SIZE variable
     * @return int : MAX_WORD_SIZE variable
     */
    public static int getMaxWordSize() {
        return MAX_WORD_SIZE;
    }

    /**
     * Static public method returning the PAGE SIZE variable
     * @return int : PAGE_SIZE variable
     */
    public static int getPageSize() {
        return PAGE_SIZE;
    }

    /**
     * Method for assigning a boolean value to the sorted variable
     * which indicates if the file is sorted or not.
     * @param sorted
     */
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    /**
     * Method for assigning a boolean value to the indexingCreated variable
     * which indicates if the indexing file is created or not.
     * @param indexingCreated
     */
    public void setIndexingCreated(boolean indexingCreated) {
        this.indexingCreated = indexingCreated;
    }

    /**
     * Method used to split each line of the file in words,
     * insert them into a list and sort them lexicographically.
     *
     * By iterating through the list we collect the words of each line
     * using special delimiters and cases, then we insert them into a list
     * and at the end we have a vector containing all the words from the whole
     * file.
     */
    private void parseWords(){
        String delimiters = "\\P{Alpha}+";  //Delimiting standard, only alphabet lettered words
        String[] result; //The array in which the result of the String.split() will be stored for each line
        int lineCount = 1; //Keeping track of the line number at which we currently are.

        fileWords.clear(); //Cleaning the list of the file words in case of a second parsing.
        System.out.println("-->Give us a moment to process the file provided...");

        //Iterating through the file lines
        for(FileLine l : fileLines){
            result = l.getText().split(delimiters);  //Split the current line in words using the delimiters set before
            lineWords = Arrays.asList(result); //Convert the array to an ArrayList which is more flexible and usable

            //Iterating through the words of the line, sorting and adjusting them to our requirementss
            for(String s : lineWords){
                if(s.length()>=MIN_WORD_SIZE) {
                    if(s.length()>MAX_WORD_SIZE){
                        s = s.substring(0,MAX_WORD_SIZE);
                    }
                    fileWords.add(new WordEntry(s, lineCount)); //If the word fits to us, then added to the formal vector
                }
            }
            lineCount++; //Moving to the next line, updating the counter...
        }
        Collections.sort(fileWords); //When the line iteration ends, the produced vector needs to be sorted
        sorted = true; //The vector was sorted successfully
    }

    /**
     * Method used to print the indexing file
     * by gradually reading pages from it and converting them
     * to lists and finally printing them.
     */
    public void print(){
        if(!indexingCreated){
            System.out.println("(!) SYSTEM WARNING (!)");
            System.out.println("-->Please create an indexing file first! Try using the 'c' command...");
            return;
        }

        //The total pages to be read
        int totalPages = (BUFFER_SIZE * fileWords.size())/PAGE_SIZE;

        //Instantiation of utilities used to access and process data from the file
        FilePageAccess fpa = new FilePageAccess(indexingPath);
        EntryConverter entcnv = new EntryConverter();

        //The list in which the word entries will be stored
        List<WordEntry> entries = new ArrayList<WordEntry>();

        //Reading the file and filling the list of word entries
        for(int i=0; i<=totalPages+1;i++){
            byte[] result = fpa.readPage(i);  //Read page i from the file
            entries.addAll(entcnv.bytesToList(result)); //Convert the bytes to list and append it to the big list
        }

        //Printing the produced list of word entries
        System.out.println("\n--- Word // Line Number ---");

        for(WordEntry s: entries){
            System.out.println("  "+s.getText() + " // " +s.getLineNumber());
        }
    }

    /**
     * Method used to search for entries in the file using a binary
     * search algorithm. The entries we look for correspond to a specific
     * search term that the user provided before the search begins.s
     *
     * The algorithm is NOT optimized and some entries might be lost or omitted!
     *
     */
    public void binarySearch(){
        //Check if the indexing file is created, so we can be sure no error or exceptions occur in the near future.
        if(!indexingCreated){
            System.out.println("(!) SYSTEM WARNING (!)");
            System.out.println("-->Please create an indexing file first! Try using the 'c' command...");
            return;
        }

        //Read the term of which the entries we are gonna look for from the console
        String termToSearch = readWord();
        System.out.println("\n-->Initializing binary search... (Term arg: "+termToSearch+")\n");

        //Instantiation of the utilities that are used to read and process data from the file.
        FilePageAccess fpa = new FilePageAccess(indexingPath);
        EntryConverter entcnv = new EntryConverter();

        //The list that stores the entries of each page
        List<WordEntry> receivedList;

        //The list that stores the results of the search procedure
        List<WordEntry> foundList = new ArrayList<WordEntry>();

        //The number of total pages that are contained in the file
        int totalPages = (BUFFER_SIZE * fileWords.size())/PAGE_SIZE;

        //Counter for keeping track of how many disk accessed were performed
        int diskAccesses = 0;

        //Three int standards used in the binary search algorithm, so that the search range can be determined
        int currentPos = 0;
        int lowerBound = 0;
        int upperBound = totalPages-1;

        //Starting a timer
        long startTime = System.nanoTime();

        //The binary search runs with no end (theoritacally). The while loop breaks when the search is completed
        while(true){
            //The currrent page position is the middle of the range in which we currently searching
            currentPos = lowerBound + (upperBound-lowerBound)/2;

            //Reading the current page and converting the bytes to word entries
            byte[] result = fpa.readPage(currentPos);
            receivedList = entcnv.bytesToList(result);
            diskAccesses++;

            //Getting the first and the last entries of tha page
            String startPivot = receivedList.get(0).getText().replaceAll(" ","");
            String endPivot = receivedList.get(receivedList.size()-1).getText().replaceAll(" ","");

            //If the term we are searching for is contained in this specific page then we search for it in the page
            //and the search ends because it won't be found anywhere else.
            if(termToSearch.compareToIgnoreCase(startPivot)>0 && termToSearch.compareToIgnoreCase(endPivot)<0){
                foundList = searchInPage(receivedList,termToSearch); // Serial search in the list of the page
                break;
            }

            //If the term whose entries we are searching is lexicographically less than the first item of the page
            //then we adjust our range so that we can continue the search to the left half of the array of pages remaining
            if(termToSearch.compareToIgnoreCase(startPivot)<0){
                upperBound = currentPos-1;
                continue; //Repeat the search again in the new range
            }

            //If the term whose entries we are searching is lexicographically more than the last item of the page
            //then we adjust our range so that we can continue the search to right half of the array of pages remaining
            if(termToSearch.compareToIgnoreCase(endPivot)>0){
                lowerBound = currentPos+1;
                continue; //Repeat the search again in the new range
            }

            //When none of the above cases is fulfilled then we have arrived at our page of interest
            //At that page we serially search for entries containing our term
            foundList = searchInPage(receivedList, termToSearch);


            //When the inside search is completed we check some more cases of close-by pages

            if(termToSearch.compareToIgnoreCase(endPivot)==0 && currentPos!=0) {
                result = fpa.readPage(currentPos + 1);
                diskAccesses++;
                receivedList = entcnv.bytesToList(result);
                foundList.addAll(searchInPage(receivedList, termToSearch));
                break;
            }

            if(termToSearch.compareToIgnoreCase(startPivot)==0 && currentPos!=0) {
                result = fpa.readPage(currentPos - 1);

                diskAccesses++;
                receivedList = entcnv.bytesToList(result);
                foundList.addAll(searchInPage(receivedList, termToSearch));
                break;
            }

            //If the close-by cases are not valid then the search ends here
            break;
        }

        //Stopping the timer and calculating the time elapsed s
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;

        //If the list of the found entries is empty then we didn't found anything and we inform the user about it
        if(!foundList.isEmpty()) {

            //If the list is too big then we ask the user if he wants still to print it to avoid traffic in the terminal
            if(foundList.size()<100){
                System.out.print("Word '" + termToSearch + "' was found in lines: ");

                for (WordEntry w : foundList) {
                    System.out.print(w.getLineNumber() + " ");
                }

            }else{
                String ans = "";
                while(!ans.equals("y") && !ans.equals("n")) {
                    ans = reader.readString("-->The generated list of entries for this word is way too long (" + foundList.size() + " items). Do you still want to print it? (y/n): ");
                }

                if(ans.equals("y")){
                    System.out.print("Word '" + termToSearch + "' was found in lines: ");
                    for (WordEntry w : foundList) {
                        System.out.print(w.getLineNumber() + " ");
                    }
                }
            }

            System.out.println("");

        }else{
            System.out.println("No entries found for the word '"+termToSearch+"'");
        }

        //Printing the statistics of the search performed previously
        System.out.println("\n------- Stats -------");
        System.out.println("Disk accesses: "+diskAccesses);
        System.out.println("Time elapsed: " +timeElapsed / 1000000L +" ms\n");
    }

    /**
     * This method is used to search serially for entries containing a specific word
     * in a given list of entries. It returns a list containing any entries matching
     * our requirements.
     * @param entries The list containing various word entries
     * @param termToSearch The word whose entries we are searching for
     * @return List<WordEntry> containing any found entries
     */
    public List<WordEntry> searchInPage(List<WordEntry> entries, String termToSearch){
        List<WordEntry> foundList = new ArrayList<WordEntry>();
        for(WordEntry w: entries){
            //We need to remove any spaces added for adjustment purposes
            if(w.getText().replaceAll(" ","").equals(termToSearch)){
                foundList.add(w); //Add any found entries to our found list
            }
        }
        return foundList;
    }

    /**
     * Method used to search for entries in the file using a serial
     * search algorithm. The entries we look for correspond to a specific
     * search term that the user provided before the search begins. The serial
     * serial stops when it finds a page containing words that are lexicographically
     * more than the term provided in order to minimize the number of meaningless disk accesses
     *
     */
    public void serialSearch(){
        //Check if the indexing file is created, so we can be sure no error or exceptions occur in the near future.
        if(!indexingCreated){
            System.out.println("(!) SYSTEM WARNING (!)");
            System.out.println("-->Please create an indexing file first! Try using the 'c' command...");
            return;
        }

        //Read the term of which the entries we are gonna look for from the console
        String termToSearch = readWord();
        System.out.println("\n-->Initializing serial search... (Term arg: "+termToSearch+")\n");

        //Instantiation of the utilities that are used to read and process data from the file.
        FilePageAccess fpa = new FilePageAccess(indexingPath);
        EntryConverter entcnv = new EntryConverter();

        //The list that stores the entries of each page
        List<WordEntry> receivedList;

        //The list that stores the results of the search procedure
        List<WordEntry> foundList = new ArrayList<WordEntry>();

        //The number of total pages that are contained in the file
        int totalPages = (BUFFER_SIZE * fileWords.size())/PAGE_SIZE;

        //Counter for keeping track of how many disk accessed were performed
        int diskAccesses = 0;

        //Starting a timer
        long startTime = System.nanoTime();

        //Repeat the search process for as many pages there are in the files
        for(int i=0; i<=totalPages;i++) {
            byte[] currentPage = fpa.readPage(i); //Read the current page
            receivedList=entcnv.bytesToList(currentPage); //Convert the bytes to entries

            //Check if it makes sense to continue the search procedure
            String pivot  = receivedList.get(0).getText().replaceAll(" ","");
            int compare = pivot.compareToIgnoreCase(termToSearch);

            if(compare>0){
                break;
            }

            //Search serially in the list produced from the current page for entries matching our term
            for(WordEntry w: receivedList){
                //We need to remove any unwanted spaces added for adjustment purposes
                if(w.getText().replaceAll(" ","").equals(termToSearch)){
                    foundList.add(w); //Add any found entries to our found list
                }
            }
            diskAccesses++;
        }

        //Stopping the timer and calculating the time elapsed
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;

        //If the list of the found entries is empty then we didn't found anything and we inform the user about it
        if(!foundList.isEmpty()) {

            //If the list is too big then we ask the user if he wants still to print it to avoid traffic in the terminal
            if(foundList.size()<100){
                System.out.print("Word '" + termToSearch + "' was found in lines: ");

                for (WordEntry w : foundList) {
                    System.out.print(w.getLineNumber() + " ");
                }

            }else{
                String ans = "";
                while(!ans.equals("y") && !ans.equals("n")) {
                    ans = reader.readString("-->The generated list of entries for this word is way too long (" + foundList.size() + " items). Do you still want to print it? (y/n): ");
                }

                if(ans.equals("y")){
                    System.out.print("Word '" + termToSearch + "' was found in lines: ");
                    for (WordEntry w : foundList) {
                        System.out.print(w.getLineNumber() + " ");
                    }
                }
            }


            System.out.println("");

        }else{
            System.out.println("No entries found for the word '"+termToSearch+"'");
        }

        //Printing the statistics of the search performed previously
        System.out.println("\n------- Stats -------");
        System.out.println("Disk accesses: "+diskAccesses);
        System.out.println("Time elapsed: " +timeElapsed / 1000000L +" ms");
        System.out.println("");
    }


    /**
     * Method used to write the indexing file page by page
     * to the disk
     * @param path The canonical path in which the indexing file will be created
     */
    public void writeIndexFile(String path){
        //Parse and sort sthe words if they aren't already
        if(!sorted){
            parseWords();
        }

        System.out.println("-->Creating Indexing File...");

        //A File object that corresponds to the file path provided
        File f = new File(path);

        //The destination path of the indexing file
        String destinationPath = f.getParentFile()+"\\"+f.getName()+".ndx";
        this.indexingPath = destinationPath;

        //Instantiation of utilities used to process and write to the file
        FilePageAccess fpa = new FilePageAccess(destinationPath);
        EntryConverter entcnv = new EntryConverter();

        //A temporal list used to store pieces of the original word entries list
        List<WordEntry> tempList = null;

        //The total number pages that are going to be written
        int totalPages = (BUFFER_SIZE * fileWords.size())/PAGE_SIZE;

        //The number of entries that fit in one disk page
        int entriesPerPage = (PAGE_SIZE/BUFFER_SIZE);

        //A int pointer used to indicate at which element we stopped cutting the list
        int stoppedAt = 0;

        //We will write a totalPages number of pages
        for(int i = 0; i<=totalPages; i++){

            //Taking as many elements as a page can fit from the list
            try {
                tempList = fileWords.subList(stoppedAt, entriesPerPage + stoppedAt-1);
            }catch(Exception e){
                //Exception occurs when the range we cut is smaller than the max number of entries per page
                try {
                    tempList = fileWords.subList(stoppedAt, fileWords.size());
                }catch (Exception ex){}
            }

            //Adjust the pointer to show at the beginning of the next set of word entries
            stoppedAt+=entriesPerPage;

            //Use the EntryConverter to convert the set of entries to bytes
            byte[] data = entcnv.listToBytes(tempList);

            //Write the byte form of the set of entries to the file at the current page position
            fpa.write(i,data);
        }

        //If the list of the word entries is not empty and it has been written in the disk
        //then at least one page exists
        if(totalPages==0 && fileWords.size()!=0){
            totalPages=1;
        }

        System.out.println("-->OK. Data pages of " +PAGE_SIZE+ " bytes : "+ totalPages);
        System.out.println("-->Indexing file '"+f.getName()+".ndx' created in: '"+destinationPath+"'");
        indexingCreated = true; //Update the boolean variable to indicate that an indexing file was created
    }

    /**
     * Method used to read and validate
     * input from the console using a StandardReader object
     * @return String : the term whose entries are going to be searched
     */
    private String readWord(){
        String termToSearch = null;

        while(termToSearch==null || termToSearch.isEmpty()){
            termToSearch = reader.readString("Type a word to search: ");
        }

        return termToSearch;
    }
}