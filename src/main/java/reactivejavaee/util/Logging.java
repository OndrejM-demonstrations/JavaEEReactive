package reactivejavaee.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    public static void logMessage(String message) {
        try {
            throw new Exception(message);
        } catch (Exception ex) {
            //Logger.getLogger(Logging.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Logger.getLogger(Logging.class.getName()).log(Level.INFO, ex.getMessage());
        }
    }
    

}
