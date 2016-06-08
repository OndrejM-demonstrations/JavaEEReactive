package reactivejavaee.examples.ejb;

import java.io.IOException;
import java.util.concurrent.*;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import reactivejavaee.util.Logging;

@WebServlet(urlPatterns = "/asyncEJB")
public class AsyncEJBServlet extends HttpServlet {

    private static final String DEFAULT_HELLO = "Hello!";
    
    @Inject
    private AsyncEJB ejb;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Logging.logMessage("AsyncEJBServlet started");
        
        Future<String> toBeHello = ejb.sayHelloToAsync("Superman");
        
        ejb.fireAndForget(req.getParameter("msg"));
    
        String hello;
        try {
            
            hello = toBeHello.get(2, TimeUnit.MINUTES);
            
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        } catch (TimeoutException ex) {
            hello = DEFAULT_HELLO;
        }
        
        resp.getOutputStream().print(hello);

        Logging.logMessage("AsyncEJBServlet finished");
    }
    
}
