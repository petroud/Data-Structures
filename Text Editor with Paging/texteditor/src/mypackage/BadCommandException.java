package mypackage;

/**
 * Exception thrown in cases of invalid commands
 */
public class BadCommandException extends Exception{

    /**
     * Class null constructor
     */
    public BadCommandException(){
        super();
    }

    /**
     * Class constructor with String argument
     * @param cmd The String message (usually command) to be carried by the exception
     */
    public BadCommandException(String cmd){
        super(cmd);
    }
}
