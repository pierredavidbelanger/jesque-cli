package ca.pjer.jesque.cli.worker;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.JobFactory;
import net.greghaines.jesque.worker.Worker;

import java.util.Collection;
import java.util.concurrent.Callable;

class WorkerFactory implements Callable<Worker> {

    protected final Config config;
    protected final Collection<String> queues;
    protected final JobFactory jobFactory;

    public WorkerFactory(Config config, Collection<String> queues, JobFactory jobFactory) {
        this.config = config;
        this.queues = queues;
        this.jobFactory = jobFactory;
    }

    public Worker call() throws Exception {
        return new InterruptionAwareWorker(config, queues, jobFactory);
    }
}
