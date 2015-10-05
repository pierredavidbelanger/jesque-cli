package ca.pjer.jesque.cli.worker;

import net.greghaines.jesque.worker.WorkerPool;

class CleanWorkerPoolShutdown implements Runnable {

    private final WorkerPool workerPool;

    public CleanWorkerPoolShutdown(WorkerPool workerPool) {
        this.workerPool = workerPool;
    }

    public void run() {
        try {
            workerPool.endAndJoin(true, 0);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for worker pool to cleanly shutdown.");
        }
    }
}
