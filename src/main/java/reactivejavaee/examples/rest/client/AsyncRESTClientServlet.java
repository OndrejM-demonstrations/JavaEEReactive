package reactivejavaee.examples.rest.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import reactivejavaee.examples.servlet.AsyncServlet;
import reactivejavaee.util.Logging;

@WebServlet(urlPatterns = "/asyncRestClient", asyncSupported = true)
public class AsyncRESTClientServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AsyncContext asyncContext = req.startAsync();

        Client client = ClientBuilder.newClient();
        WebTarget resourceTarget = client.target("http://localhost:8080/JavaEEReactive/rest/async");

        // Build and invoke the get request asynchronously in a single step
        // We don't use Future but InvocationCallback, as it does not block any threads
        Logging.logMessage("Calling REST resource");
        Future response = resourceTarget.request(MediaType.TEXT_PLAIN)
                .async().get(new InvocationCallback<String>() {
                    @Override
                    public void completed(String response) {
                        Logging.logMessage("Got response from REST resource");
                        new AsyncServlet.AsyncServletWriter(asyncContext)
                            .write("Response from REST resource: "+ response, StandardCharsets.UTF_8)
                            .thenRun(() -> {
                                    Logging.logMessage("Reponse from servlet prepared");
                                })
                            .thenRun(asyncContext::complete)
                            .thenRun(() -> {
                                Logging.logMessage("Reponse from servlet completed");
                            });
                            
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        // we need to handle also failures and complete the request,
                        // otherwise request will not stop until timeout
                        HttpServletResponse resp = (HttpServletResponse)asyncContext.getResponse();
                        resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                        asyncContext.complete();
                    }
                });

    }
}
