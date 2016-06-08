package reactivejavaee.examples.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("async")
public class AsyncRESTResource {
    @GET
    public void whatIsTheAnswer(@Suspended AsyncResponse r) {
        r.resume("42");
    }
}
