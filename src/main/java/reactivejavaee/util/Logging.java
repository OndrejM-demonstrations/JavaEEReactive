package reactivejavaee.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import reactivejavaee.examples.ejb.AsyncEJB;

public class Logging {
    public static void logMessage(String message) {
        try {
            throw new Exception(message);
        } catch (Exception ex) {
            //Logger.getLogger(AsyncEJB.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Logger.getLogger(AsyncEJB.class.getName()).log(Level.INFO, ex.getMessage());
        }
    }
    

}
