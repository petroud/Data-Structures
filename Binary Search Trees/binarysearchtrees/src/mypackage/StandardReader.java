package mypackage;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Dimitrios Petrou
 * @original_author Giorgos Anestis / TUC_MUSIC
 *
 *  This class offers various methods for console input reading for different types of inputs
 *
 *  Updated version of the StandardInputRead class
 */

public class StandardReader {

    public StandardReader() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }

    public final static int POSITIVE_MALF = -1;
    public final static int NEGATIVE_MALF = 1;

    private BufferedReader input;

    public String readString(String message) {
        System.out.print(message);
        try {
            return input.readLine();
        }
        catch (Exception e) { return null; }
    }

    public int readPositiveInt(String message) {
        String tempStr;
        int num;

        System.out.print(message);
        try {
            tempStr = input.readLine();
            num = Integer.parseInt(tempStr);
            if (num < 0 ){
                return POSITIVE_MALF;
            }
            else {
                return num;
            }
        }
        catch (IOException e) { return POSITIVE_MALF; }
        catch (NumberFormatException e1) { return NEGATIVE_MALF; }
    }

    public int readNegativeInt(String message) {
        String tempStr;
        int num;

        System.out.print(message);
        try {
            tempStr = input.readLine();
            num = Integer.parseInt(tempStr);
            if (num >= 0 ){
                return NEGATIVE_MALF;
            }
            else {
                return num;
            }
        }
        catch (IOException e) { return NEGATIVE_MALF; }
        catch (NumberFormatException e1) { return NEGATIVE_MALF; }
    }
}
