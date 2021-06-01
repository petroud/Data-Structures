package mypackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * The Commander class implements most of the features of the text editors and
 * handles appropriately the list with the lines and various actions
 * that are based on them. It uses an Indexer object in order to provide the
 * required methods for the indexing procedures and searches.
 */
@SuppressWarnings("ALL")
public class Commander {

    /**
     * filePath member variable is used to store the canonical path of the txt file provided
     */
    private String filePath;

    /**
     * fileName member variable is used to store the name of the txt file provided
     */
    private String fileName;

    /**
     * currentLine mebmer variable is used for keeping track of on which line we currently are among the list of file lines
     */
    private FileLine currentLine;

    /**
     * currentLineIDX member variable is used for storing the number of the current line according to the lists
     */
    private int currentLineIDX;

    /**
     * fileOpened member variable is used for knowning when a file is opened or not (true/false)
     */
    private boolean fileOpened;

    /**
     * idxDisplayed member variable is used for knowning when to print the index of each line while printing them
     */
    private boolean idxDisplayed;

    /**
     * The list of lines that are read from the file
     */
    private LinkedList<FileLine> fileLines;

    /**
     * A StandardReader object used for reading console input
     */
    private StandardReader reader;

    /**
     * An Indexer object used for the indexing and search procedures on the file
     */
    private Indexer indexer;

    /**
     * A static member variable used to specify the max number of characters in a line
     */
    private static int MAX_CHARACTERS_PER_LINE;


    /**
     * Class constructor with String and int arguments
     * @param pth The path of the file to be edited (Can be left empty)
     * @param MAX_CHARS The max number of characters per line allowed to be read
     */
    public Commander(String pth, int MAX_CHARS){

        //Instatiation of the objects
        reader = new StandardReader();
        fileLines = new LinkedList<FileLine>();
        indexer = new Indexer(fileLines);

        this.MAX_CHARACTERS_PER_LINE = MAX_CHARS;
        this.filePath = pth;

        //Initiliazing some member variables
        fileName = null;  //The parseFile() method will assign a value later
        fileOpened = false; //The file is not yet opened
        idxDisplayed = true; //The index is factory set to be displayed
        parseFile(); //Process the file and import it
    }


    //-------------------------------------------------------------------------------------------//
    //--> Methods that implement the functionality of the text editor

    /**
     * A method that prints all the lines of the file by
     * iterating through the lists in which they are
     * stored.
     */
    private void printLines(){
        if(fileOpened) {
            int lineIdx = 1;
            for (FileLine l : fileLines) {
                if(idxDisplayed) {
                    System.out.println(lineIdx + ") " + l.getText());
                }else{
                    System.out.println(" "+l.getText());
                }
                lineIdx++;
            }
        }else{
            System.out.println("No file specified");
        }
    }


    /**
     * A method that counts the lines and characters of the file
     * by iterating through the list of lines for counting.
     */
    private void countLinesAndChars(){
        if(fileOpened) {
            int lineIdx = 0;
            int totalLength = 0;
            for (FileLine l : fileLines) {
                lineIdx++;
                totalLength += l.getText().length();
            }
            System.out.println(lineIdx+" lines, "+totalLength+" characters");
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * A method for printing the current line
     * by using the currentLine and currentLineIDX member variables.
     */
    private void printCurrentLine(){
        if(fileOpened) {
            System.out.println(currentLineIDX + 1 + ") " + currentLine.getText());
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * A method for printing the current line index
     * by using the currentLineIDX member variable.
     */
    private void printCurrentLineIDX(){
        if(fileOpened){
            System.out.println("Line #"+currentLineIDX+1);
        }else{
            System.out.println("No file specified");
        }
    }


    /**
     * A method for deleting the current line from the list
     * and updating the references of the close-by nodes.
     *
     * It works for first and last lines too by catching
     * exceptions and errors. It handles each case respectively.
     *
     * After each successful delete the first line of the list
     * becomes the first line
     *
     * The list stores at least one line, even if it's emtpy.
     *
     * In order to avoid conflicts, omissions and errors in the
     * indexing process it is obligatory to re-index the file
     * after changes have been done.
     *
     */
    private void deleteCurrentLine(){
        if(fileOpened){
            fileLines.remove(currentLine); //Remove the line from the list
            try{
                currentLine.getPrevious().setNext(currentLine.getNext()); // Try updating the 'next' reference of the previous node
            }catch(NullPointerException ex){
                //An exception occurs while trying to update references
                //of non existent nodes (e.g. the previous of the first node does not exist)

                //If the line we removed earlier is the last node of the list
                //then the previous of it becomes the new last line
                try {
                    currentLine.getPrevious().setNext(null); // If last line is deleted then the previous of it does not have next
                }catch (NullPointerException ex1){}
            }


            try{
                currentLine.getNext().setPrevious(currentLine.getPrevious());
            }catch(NullPointerException ex){
                //An exception occurs when trying to update references
                //of non existent nodes (e.g. the next of last node does not exist

                //If the line we removed earlier is the first node of the list
                //then the next of it becomes the new first line
                try {
                    currentLine.getNext().setPrevious(null); //If first line is deleted then the next of it does not have previous
                }catch(NullPointerException ex1){}
            }

            //Updating the currentLine member variable to
            //show to the first line of the list
            try {
                currentLine = fileLines.getFirst();
                currentLineIDX = fileLines.indexOf(currentLine);
                System.out.println("OK");
            }catch(NoSuchElementException ex){
                //If the list is empty then add a new empty
                //FileLine
                currentLine = new FileLine("");
                fileLines.add(currentLine);
                currentLineIDX = fileLines.indexOf(currentLine);
                System.out.println("OK");
            }

            //Require the user to index the file again
            indexer.setSorted(false);
            indexer.setIndexingCreated(false);
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method to invert the boolean member variable idxDisplayed
     * which specifies if the number of the line is displayed or not
     * while printing the list
     */
    private void toggleIdx(){
        idxDisplayed = !idxDisplayed;
    }


    /**
     * Method to point the member variable currentLine to the first
     * line of the list of lines.
     */
    private void moveToFirstLine(){
        if(fileOpened) {
            try {
                currentLine = fileLines.getFirst();
                currentLineIDX = fileLines.indexOf(currentLine);
                System.out.println("OK");
            } catch (Exception e) {
            }
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method to point the member variable currentLine
     * to the previous line of it, using its previous reference
     */
    private void moveOneLineUp(){
        if(fileOpened) {
            try {
                currentLine = currentLine.getPrevious();  //Pointing the currentLine to the previous of it
                currentLineIDX = fileLines.indexOf(currentLine);  //Updating the index of the current line to the new one
                currentLine.getText(); //Causing an exception to check if the currentLine = null
                System.out.println("OK");
            } catch (Exception e) {
                //Exception occurs when trying to move to the previous node
                //of the first line
                moveToFirstLine(); //We stay at the first line
            }
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method to point the member variable currentLine to the last
     * line of the list of lines.
     */
    public void moveToLastLine(){
        if(fileOpened) {
            try {
                currentLine = fileLines.getLast();
                currentLineIDX = fileLines.indexOf(currentLine);
                System.out.println("OK");
            } catch (Exception e) {
            }
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method to point the member variable currentLine
     * to the next line of it, using its next reference
     */
    private void moveOneLineDown(){
        if(fileOpened) {
            try {
                currentLine = currentLine.getNext(); //Pointing the currentLine to the next of it
                currentLineIDX = fileLines.indexOf(currentLine); //Updating the index of the current line to the new one
                currentLine.getText(); //Causing an exception to check if the currentLine = null;
                System.out.println("OK");
            } catch (Exception e) {
                //Exception occurs when trying to move to the next node
                //of the last line
                moveToLastLine(); //We stay at the last line
            }
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method used to read console input and return it
     * as a FileLine object, ready to be inserted in
     * the list. For the reading of the console input
     * the reader object (StandardReader) is used.
     *
     * @return FileLine object with the user input as text member variable
     */
    private FileLine readNewLine(){
        String userLineText = null;

        //Repeat the request as long as the user's input is null
        while(userLineText==null) {
            userLineText = reader.readString("Type text for new line: \n");
            if(userLineText==null){
                System.out.println("New line can't be left null or empty");
            }
        }

        FileLine newLine = new FileLine(userLineText);
        return newLine;
    }

    /**
     * Method used to edit the text of the line that corresponds to
     * the currentLine member variable.
     *
     *  In order to avoid conflicts, omissions and errors in the
     *  indexing process it is obligatory to re-index the file
     *  after changes have been done.
     */
    private void editCurrentLine(){
        if(fileOpened){
            String newText = reader.readString("Please enter the new text for the current line: \n");
            currentLine.setText(newText);
            indexer.setSorted(false);
            indexer.setIndexingCreated(false);
            System.out.println("OK");
        }else{
            System.out.println("No file specified");
        }
    }


    /**
     * Method used to add a new line after the current line
     * after reading a specific text for it from the user
     *
     * It handles exceptions and error cases respectively
     * and it uses the node references to reconnect
     * the list nodes correctly.
     *
     * It also modifies the user's input to meet the max
     * number of characters per line requirements.
     *
     * In order to avoid conflicts, omissions and errors in the
     * indexing process it is obligatory to re-index the file
     * after changes have been done.
     */
    private void addLineAft() {
        if (fileOpened) {
            FileLine newLine = readNewLine();  //Read the user's input using a method

            try {
                //Modify the text of the new line to meet the requirements for
                //the max number of characters per line.
                newLine.setText(newLine.getText().substring(0, MAX_CHARACTERS_PER_LINE));
            } catch(Exception e){}

            //Setting the references of the new line
            try {
                //The line is added after the current line.
                //We set its previous reference to show to the currentLine
                newLine.setPrevious(currentLine);
                //The next reference of the new line is now showing to
                //the next line of the currentLine
                newLine.setNext(currentLine.getNext());
            } catch (NullPointerException e) {
                newLine.setNext(null);
            }

            //Updating the references of the currentLine
            try {
                //The next node after the currentLine must be updated
                //that its previous line is now the new one
                currentLine.getNext().setPrevious(newLine);
                //Then the currentLine must know that the next of it
                //is the new line
                currentLine.setNext(newLine);
            } catch (NullPointerException e) {
                currentLine.setNext(newLine);
            }

            //Add the new line in the list after the currentLine
            //using its index (+1) to specify the position.
            fileLines.add(currentLineIDX + 1, newLine);

            //Require the user to re-index the file because changes
            //have taken place.
            indexer.setSorted(false);
            indexer.setIndexingCreated(false);
            System.out.println("OK");
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method used to add a new line before the current line
     * after reading a specific text for it from the user
     *
     * It handles exceptions and error cases respectively
     * and it uses the node references to reconnect
     * the list nodes correctly.
     *
     * It also modifies the user's input to meet the max
     * number of characters per line requirements.
     *
     * In order to avoid conflicts, omissions and errors in the
     * indexing process it is obligatory to re-index the file
     * after changes have been done.
     */
    private void addLineBef(){
        if(fileOpened) {
            FileLine newLine = readNewLine(); //Read the user's input using a method

            try {
                //Modify the text of the new line to meet the requirements for
                //the max number of characters per line.
                newLine.setText(newLine.getText().substring(0, MAX_CHARACTERS_PER_LINE));
            } catch(Exception e){}

            //Setting the references of the new line
            try {
                //The line is added before the current line.
                //We set its next reference to show to the currentLine
                newLine.setNext(currentLine);
                //The next reference of the new line is now showing to
                //the next line of the currentLine
                newLine.setPrevious(currentLine.getPrevious());
            } catch (NullPointerException e) {
                newLine.setPrevious(null);  //Not all currentLines have previous nodes. (e.g. the first one)
            }

            //Updating the references of the currentLine
            try {
                //The previous node before the currentLine must be updated
                //that its next line is now the new one
                currentLine.getPrevious().setNext(newLine);
                //Then the currentLine must know that the previous of it
                //is the new line
                currentLine.setPrevious(newLine);
            } catch (NullPointerException e) {
                currentLine.setPrevious(newLine); //Not all current line have previous nodes.
            }

            //We insert the line in the list at the current index pos
            try {
                fileLines.add(currentLineIDX, newLine);
            } catch (Exception e) {
                //If the current line is the first line the we push the new line in the list
                fileLines.addFirst(newLine);
            }

            //Update the current index variable, so the current line index can keep up with the list.
            currentLineIDX++;

            //Require the user to re-index the file because changes
            //have taken place.
            indexer.setSorted(false);
            indexer.setIndexingCreated(false);
            System.out.println("OK");

        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method used to write and save the file to the disk.
     * Because it's meaningless to track changes in the list
     * every time we want to save the file we overwrite it by writing
     * line by line to the disk.
     */
    private void writeFileToDisk(){
        if(fileOpened){
            try {
                System.out.print("Saving...");
                PrintWriter pr = new PrintWriter(filePath);
                if(fileLines.size()>0) {
                    for (FileLine l : fileLines) {
                        pr.write(l.getText() + "\n");
                    }
                }else{
                    pr.write("");
                }
                pr.close();
                System.out.println("OK");
            } catch (FileNotFoundException e) {
                System.out.println("Error occurred while saving the file");
            }

        }else {
            System.out.println("No file specified");
        }
    }

    /**
     * Method used to call the method Indexer.print(), using the
     * indexer object declared earlier, that prints
     * the words written in the indexing file.
     */
    private void printFileWords(){
        if(fileOpened) {
            indexer.print();
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method that calls the method (Indexer.binarySearch()) for searching in a binary
     * way for entries of a specific word in the indexing file
     */
    private void binarySearch(){
        if(fileOpened) {
            indexer.binarySearch();
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method that calls the method (Indexer.serialSearch()) for searching in a serial
     * way for entries of a specific word in the indexing file
     */
    private void serialSearch(){
        if(fileOpened) {
            indexer.serialSearch();
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method used to call the method of the Indexer used to
     * build and write the indexing file.
     */
    private void createIndexFile() {
        if (fileOpened){
            indexer.writeIndexFile(filePath);
        }else{
            System.out.println("No file specified");
        }
    }

    /**
     * Method that is used to print all the info about
     * each line in the list
     */
    public void debug(){
        for(FileLine l : fileLines){
            l.debugprintInfo();        }
    }
    //------------------------------------------ END --------------------------------------------//




    //-------------------------------------------------------------------------------------------//
    //--> Methods that are related to file handling procedures

    /**
     * Method that is used to read the txt file provided, create the list of lines and
     * set all the needed variables appropriately.
     *
     * If the user hasn't provided a txt file while launching the program, then a new one is
     * created automatically after the user is asked for permission.
     */
    private void parseFile() {
        /**
         * A File object that corresponds to the txt file provided,
         * according to the filepath.
         */
        File fileObj = new File(filePath);

        /**
         * A FileLine variable used to keep track of the last
         * line that was read from the file
         */
        FileLine lastRead = null;

        /**
         * A FileLine variable used to keep track of the line
         * that is being read currently.
         */
        FileLine currentRead = null;

        //Processing the file line by line
        try {
            checkFile(fileObj);  //Check if the file provided meets the minimum requirements of the program

            Scanner sc = new Scanner(fileObj);  //Creating a Scanner object, ready to be used to read filess.

            //Read and insert new lines as long as the file has more lines to be read
            while(sc.hasNextLine()){
                String inputLine = sc.nextLine();       // Getting the text from the file using a scanner obj

                //Adjusting the text of each to line so it can meet the
                //requirements about the maximum number of characters per line
                if(inputLine.length()>MAX_CHARACTERS_PER_LINE){
                    inputLine = inputLine.substring(0,MAX_CHARACTERS_PER_LINE);
                }

                //Set the currentRead variable to have as a value a new FileLine object
                //that corresponds to the line that was read now from the Scanner
                currentRead = new FileLine(inputLine);  // The file line being read now

                //Set the node references of the new FileLine
                try{
                    currentRead.setPrevious(lastRead);  // We may have a previous line to update the reference or we may not (Exception occurs for the first line)
                    lastRead.setNext(currentRead);      // If we have a previous line then we update the reference of it for its next line (Exception occurs for the last line)
                } catch (NullPointerException ex){}

                fileLines.add(currentRead);             // Current line added in the Linked List of FileLines
                lastRead = currentRead;                 // The last line for the next while loop cycle will be the current line of the previous one
            }

            //Close the scanner object
            sc.close();

            // File operations and validation variables
            fileOpened = true;
            fileName = fileObj.getName();

            // Current line variable reference update
            try{
                currentLine = fileLines.get(0);
                currentLineIDX = fileLines.indexOf(currentLine);
            }catch (IndexOutOfBoundsException e){
                currentLine = new FileLine("");
                fileLines.add(currentLine);
                currentLineIDX = fileLines.indexOf(currentLine);
            }

        } catch (FileNotFoundException e) {
            //If the provided is not valid then an exception occurs,
            //the user is now asked to answer if a new file will be created
            //or not.
            System.out.println("(!) SYSTEM WARNING (!)");
            System.out.println("--> The path points to a file either: a)non-readable b)non-writable c)not a file d)non-existent");

            String answer = "";
            while(!answer.equals("y") && !answer.equals("n")) {
                answer = reader.readString("--> Do you want to create a new file (y/n)? : ");
            }

            if(answer.equals("y")){
                filePath = getNewFileName(); //Get a random name for the new file
                createNewFile(); //Write the new file to the disk

                fileObj = new File(filePath);  // The fileObj is now the file that was automatically created

                //Updating the references for the file path in the disk
                try {
                    String canonicalPath = fileObj.getCanonicalPath();
                    filePath = canonicalPath;
                }catch(Exception e1){
                }

                //Informing the user about the location of the new file
                System.out.println("--> File created in: '" +filePath+"'");
                parseFile(); //Parsing the file and creating the list
                System.out.println("--> File name: '" +fileName+"'");
            }
        }
    }

    /**
     * Method that creates a random String
     * and appends to it the extension .txt
     * so it can be used as file name.
     * @return String: the random file name.
     */
    private String getNewFileName(){
        Random rand = new Random();
        int n = rand.nextInt(20)+1;

        String temp = "newfile";
        temp += "_"+String.valueOf(n);

        return temp+".txt";
    }

    /**
     * Method that is used to write the
     * initial file created from the system
     * if the user didn't provided a txt file
     * when launching the program.
     */
    private void createNewFile(){
        try {
            PrintWriter pr = new PrintWriter(filePath);
            pr.write("");
            pr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
    private void checkFile(File fileToCheck) throws FileNotFoundException {
        if(fileToCheck.exists() && fileToCheck.canRead() && fileToCheck.canWrite() && fileToCheck.isFile() && filePath!=null && !filePath.isEmpty()){
            return;
        }else{
            fileOpened = false;
            throw new FileNotFoundException();
        }
    }
    //------------------------------------------ END --------------------------------------------//



    //-------------------------------------------------------------------------------------------//
    //--> Methods that are related to commands for their execution & validation procedures

    /**
     * Method that is used to read a String command from the console
     * and return it.
     * @return String : the command read from the console
     */
    private String readCommand(){
        String readStr = reader.readString(getPrompt());
        return readStr;
    }

    /**
     * Method that checks if a command is:
     * - 1 character long
     * - not empty
     * - not null
     * @return The command that was read if it's valid
     * @throws BadCommandException if at least one of the requirements above is not met
     */
    public String validateCommand() throws BadCommandException {
        String cmd = readCommand();
        if(cmd.length()>1 || cmd.isEmpty() || cmd == null){
                throw new BadCommandException(cmd);
        }
        return cmd;
    }

    /**
     * Method that is used to invoke methods based on the
     * input command using a switch case module.
     *
     * @param cmd The valid command the user gave in the console
     * @throws BadCommandException if a not valid command arrives here
     * @throws TerminateException when the appropriate command is given and the program terminates
     */
    public void executeCommand(String cmd) throws BadCommandException, TerminateException {
        String command = cmd;

        switch(command) {
            case "^":
                this.moveToFirstLine();
                break;
            case "$":
                this.moveToLastLine();
                break;
            case "-":
                this.moveOneLineUp();
                break;
            case "+":
                this.moveOneLineDown();
                break;
            case "=":
                this.printCurrentLineIDX();
                break;
            case "l":
                this.printLines();
                break;
            case "#":
                this.countLinesAndChars();
                break;
            case "d":
                this.deleteCurrentLine();
                break;
            case "n":
                this.toggleIdx();
                break;
            case "p":
                this.printCurrentLine();
                break;
            case "w":
                this.writeFileToDisk();
                break;
            case "a":
                this.addLineAft();
                break;
            case "t":
                this.addLineBef();
                break;
            case "e":
                this.editCurrentLine();
                break;
            case "~":
                this.debug();
                break;
            case "c":
                this.createIndexFile();
                break;
            case "v":
                this.printFileWords();
                break;
            case "s":
                this.serialSearch();
                break;
            case "b":
                this.binarySearch();
                break;
            case "q":
                System.out.println("Exiting...");
                throw new TerminateException();
            case "x":
                this.writeFileToDisk();
                System.out.println("Exiting...");
                throw new TerminateException(); //Throw the exception to be caught
            default:
                throw new BadCommandException(cmd); //Throw a BadCommandException
        }
    }

    /**
     * Method used to create a String prompt
     * to be displayed to the user. It is formed
     * in 2 ways:
     * - FILENAME/cmd: (The user knows on which file he is taking action)
     * - cmd: (When a file is either not specified nor created.
     * @return String : The prompt to be displayed to the user
     */
    private String getPrompt() {
        if(fileName!=null && !fileName.isEmpty()) {
            return "\n"+fileName+"\\CMD> ";
        }else{
            return "\nCMD> ";
        }
    }
    //----------------------------------------- END --------------------------------------------//
}