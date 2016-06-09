package reactivejavaee.examples.rest;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.container.AsyncResponse;
import reactivejavaee.remote.RemoteResource;
import reactivejavaee.util.Logging;

@Asynchronous
@Stateless
public class AsyncAnswerer {
    
    @Inject
    private RemoteResource resource;
    
    public void giveAnswerToResponse(AsyncResponse response) {
        resource.accessResource();
        response.resume("42");
        Logging.logMessage("REST response completed");
    }
}
