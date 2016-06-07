package reactivejavaee.cdi.control;

import java.util.concurrent.ExecutorService;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import reactivejavaee.cdi.boundary.Managed;

@ApplicationScoped
public class ManagedExecutorServiceProducer {
    
    @Resource
    private ManagedExecutorService executorService;
    
    @Produces
    @Managed
    @Dependent
    public ExecutorService produceExecutorService() {
        return executorService;
    }
    
}