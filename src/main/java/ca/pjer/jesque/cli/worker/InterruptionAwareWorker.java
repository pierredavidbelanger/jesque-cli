package ca.pjer.jesque.cli.worker;

import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.worker.JobFactory;
import net.greghaines.jesque.worker.WorkerImpl;

import java.util.Collection;

class InterruptionAwareWorker extends WorkerImpl {

    public InterruptionAwareWorker(Config config, Collection<String> queues, JobFactory jobFactory) {
        super(config, queues, jobFactory);
    }

    @Override
    protected void failure(Throwable t, Job job, String curQueue) {
        if (t instanceof InterruptedException) return;
        super.failure(t, job, curQueue);
    }
}
