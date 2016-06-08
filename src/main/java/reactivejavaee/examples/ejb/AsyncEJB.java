package reactivejavaee.examples.ejb;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import javax.ejb.*;
import javax.servlet.AsyncContext;
import static reactivejavaee.examples.servlet.AsyncServlet.AsyncServletWriter;
import reactivejavaee.util.Logging;

// all methods are asynchronous
@Asynchronous
@Stateless
public class AsyncEJB extends AsyncEJBBase {

    public Future<String> sayHelloToAsync(String person) {
        return new AsyncResult<>(super.sayHelloTo(person));
    }

    @Override
    public void fireAndForget(String message) {
        super.fireAndForget(message);
    }

    public void sayHelloToAsync(String superman, AsyncContext asyncContext) {
        String hello = super.sayHelloTo(superman);
        new AsyncServletWriter(asyncContext)
                            .write(  hello
                                    , StandardCharsets.UTF_8);
        asyncContext.complete();
        
        Logging.logMessage("Response finished in another thread");
    }

    

}
