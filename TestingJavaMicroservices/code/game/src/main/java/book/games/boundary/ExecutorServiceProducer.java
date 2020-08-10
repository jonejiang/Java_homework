package book.games.boundary;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutorService;

@ApplicationScoped
public class ExecutorServiceProducer {

    @Resource
    ManagedExecutorService managedExecutorService;

    public ExecutorService getManagedExecutorService() {
        return managedExecutorService;
    }
}
