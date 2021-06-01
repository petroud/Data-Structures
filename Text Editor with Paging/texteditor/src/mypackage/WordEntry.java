package mypackage;

/**
 * Class for storing a word and the line in which it was found
 */
public class WordEntry implements Comparable{

    /**
     * Member variable for storing the word itself
     */
    private String text;

    /**
     * Member variable for storing the number of the line in which it was found
     */
    private int lineNumber;

    /**
     * Class constructor with String,int args
     * @param t String: for giving the word itself
     * @param i int: for giving the line number in which it was found
     */
    public WordEntry(String t, int i){
        this.text = t;
        this.lineNumber = i;
    }

    /**
     * Method for getting the word itself from a WordEntry
     * @return String: the text member variable of the object
     */
    public String getText() {
        return text;
    }

    /**
     * Method for getting the line number from a WordEntry
     * @return int: the lineNumber member variable of the object
     */
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    /**
     * Method for returning a comparison result between two WordEntry objects
     * according to their text member variables.
     * @param Object : An Object casted to WordEntry, so its text member variable can be compared.s
     * @return int : the result of the comparison of the strings
     *   0: equal words
     *  +1: this word is bigger alphabetically than the one given as an arg
     *  -1: this word is less alphabetically than the one given as an arg
     */
    public int compareTo(Object obj) {
        WordEntry remote = (WordEntry)obj;  //Cast the object arg to WordEntry

        String localText = this.getText();  //Get the text of this WordEntry object
        String remoteText = remote.getText(); //Get the text of the remote WordEntry object

        int compareResult = localText.compareToIgnoreCase(remoteText); // Compare the two strings and store the result

        return compareResult; //Return the result
    }
}
