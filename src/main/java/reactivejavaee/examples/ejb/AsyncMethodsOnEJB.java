package reactivejavaee.examples.ejb;

import javax.ejb.*;

@Stateless
public class AsyncMethodsOnEJB extends AsyncEJBBase {
    
    @Asynchronous
    @Override
    public void fireAndForget(String message) {
        super.fireAndForget(message);
    }

    // executes synchronously - blocks until remote resource is accessed
    @Override
    public String sayHelloTo(String person) {
        return super.sayHelloTo(person);
    }

}
