package reactivejavaee.examples.ejb;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import reactivejavaee.util.Logging;

@WebServlet(urlPatterns = "/fullyAsyncEJB", asyncSupported = true)
public class FullyAsyncEJBServlet extends HttpServlet {

    private static final String DEFAULT_HELLO = "Hello!";
    
    @Inject
    private AsyncEJB ejb;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        Logging.logMessage("AsyncEJBServlet started");
        
        ejb.sayHelloToAsync("Superman", req.startAsync());
        
        ejb.fireAndForget(req.getParameter("msg"));
    
        Logging.logMessage("AsyncEJBServlet finished");
    }
    
}
