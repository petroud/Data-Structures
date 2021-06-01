package mypackage;

/**
 * MyEditor class contains the main method
 * and features some simple procedures such as
 * - The instantiation of a Commander object
 * - The method for printing a simple console logo
 * - Passing the arguments of the user to the program
 * - Initiating the method for accepting commands.
 *  The program can be launched from any terminal by using
 *  the command java mypackage.MyEditor $-FILEPATH-$(opt) after
 *  compilation.
 */
public class MyEditor {

    /**
     * A String member variable for storing the user's input
     */
    private String currentCommand;

    /**
     * A Commander member variable for the implementation of the required features
     */
    private Commander commander;

    /**
     * Class Constructor
     * @param path The canonical path to the txt file given by the user before launching the program
     */
    public MyEditor(String path){
        this.printOutline(); // Printing a simple console logo, just for aesthetic
        this.commander = new Commander(path,80); // Instantiation of a Commander object with string argument for the file path and an int argument for the MAX_CHARACTERS_PER_LINE
        this.currentCommand = "-1"; // Initiliazing the currentCommand variable to -1, which is not a valid command for the program.
        this.acceptCommands(); // Calling the method for accepting commands from the console and executing them.
    }

    /**
     * Method for accepting and checking commands and eventually executing them if they are valid.
     * This process repeats endlessly after each successful command acceptance.
     * When a TerminateException is thrown, the while loop breaks and the procedure terminates.
     */
    public void acceptCommands() {
        while(true){
            try {
                currentCommand = commander.validateCommand(); //Checking if the command is valid and correctly typed
                commander.executeCommand(currentCommand);     //If the command is valid it is executed and handled by the Commander
            } catch (BadCommandException e1) {
                System.out.println("Unknown command: '"+e1.getMessage()+"'"); // If the command is not valid, an exception is thrown by
                                                                              // the Commander.validateCommand() method and the user is
                                                                              // informed accordingly.
            } catch (TerminateException e) {
                return;  //Terminating the method and so the program
            }
        }
    }

    /**
     * Method for printing a simple text art logo in the console
     */
    private void printOutline() {
        System.out.println("--- v1.0 ----------------------------------------------");
        System.out.println("      ___  ___     ___     ___  __    ___  __   __  \r\n" +
                "       |  |__  \\_/  |     |__  |  \\ |  |  /  \\ |__) \r\n" +
                "       |  |___ / \\  |     |___ |__/ |  |  \\__/ |  \\ \r\n");
        System.out.println("--- COMP202_TUC | 2020 ----------------- By dpetrou ---\n");
    }

    /**
     * The main method called from the system that launches the software.
     * @param args The array of String arguments given in the terminal (args[0] is the filepath)
     */
    public static void main(String[] args){
        try{
            //The filepath is given as an argument to the MyEditor object
            MyEditor me = new MyEditor(args[0]);
        }catch(Exception e){
            //If the path is null (The user didn't gave anything as argument) then we give an empty string as an arg.
            MyEditor me = new MyEditor(" ");
        }
    }
}