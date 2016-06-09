package reactivejavaee.examples.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import reactivejavaee.util.Logging;

@Path("async")
@RequestScoped
public class AsyncRESTResource {
    
    @Inject
    private AsyncAnswerer answerer;
    
    @GET
    public void whatIsTheAnswer(@Suspended AsyncResponse response) {
        answerer.giveAnswerToResponse(response);
        Logging.logMessage("REST resource handler finished");
    }
}
