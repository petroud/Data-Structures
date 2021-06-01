package mypackage;

/**
 * Class for creating a Node for the list containing a line of the txt file
 * and references to the next and previous nodes of it.
 */
@SuppressWarnings("unused")
public class FileLine {

    /**
     * String member variable for storing the text of the line
     */
    private String text;

    /**
     * FileLine member variable for keeping a reference to the previous node
     */
    private FileLine previous;

    /**
     * FileLine member variable for keeping a reference to the next node
     */
    private FileLine next;


    /**
     * Class constructor with String argument
     * @param txt : The text of the line corresponding to this node
     */
    public FileLine(String txt) {
        this.text = txt;
        this.previous = null;
        this.next = null;
    }

    /**
     * Class null constructor
     */
    public FileLine(){
        super();
    }


    /**
     * Method for getting the text of a node
     * @return String : the text member variable of the node
     */
    public String getText() {
        return text;
    }

    /**
     * Method for getting the reference to the previous node of the this node
     * @return FileLine: the previous member variable of the node
     */
    public FileLine getPrevious() {
        return previous;
    }

    /**
     * Method for getting the reference to the next node of the this node
     * @return FileLine: the next member variable of the node
     */
    public FileLine getNext() {
        return next;
    }

    /**
     * Method for setting the text of the node
     * @param text : the desirable text to be setted as the text member variable of the node
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Method for setting the reference of the previous node
     * @param previous : the desirable previous FileLine node
     */
    public void setPrevious(FileLine previous) {
        this.previous = previous;
    }

    /**
     * Method for setting the reference of the next node
     * @param next : the desirable next FileLine node
     */
    public void setNext(FileLine next) {
        this.next = next;
    }


    /**
     * Method for printing every information about a line
     */
    public void debugprintInfo(){
        System.out.println("------------ Line -------------");
        System.out.println("Text: "+this.getText());
        System.out.println("--------");
        try {
            System.out.println("Previous text  : " + this.getPrevious().getText());
        }catch(Exception e){}
        try {
            System.out.println("Next text      : " + this.getNext().getText());
        }catch(Exception e){}
        System.out.println("-------------------------------\n");
    }
}