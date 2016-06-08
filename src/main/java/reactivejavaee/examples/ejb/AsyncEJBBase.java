package reactivejavaee.examples.ejb;

import javax.inject.Inject;
import reactivejavaee.remote.RemoteResource;
import reactivejavaee.util.Logging;

public abstract class AsyncEJBBase {
    @Inject
    private RemoteResource remoteResource;

    protected void fireAndForget(String message) {
        Logging.logMessage("Fire and forget executed with message=" + message);
        remoteResource.accessResource();
        Logging.logMessage("Fire and forget finished with message=" + message);
    }

    protected String sayHelloTo(String person) {
        Logging.logMessage("Say hello started for person=" + person);
        final String result = "Hello, " + person +"!";
        remoteResource.accessResource();
        Logging.logMessage("Say hello finished with result=" + result);
        return result;
    }


}
