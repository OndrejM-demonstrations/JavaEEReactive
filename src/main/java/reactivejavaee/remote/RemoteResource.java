package reactivejavaee.remote;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;

@Dependent
public class RemoteResource {

    /**
     * Potentially long running task - we simulate using Thread.sleep()
     */
    public void accessResource() {
        try {
            
            Thread.sleep(2000);
            
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
