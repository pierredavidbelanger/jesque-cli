package ca.pjer.jesque.cli.worker;

import ca.pjer.jesque.cli.Command;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.validators.PositiveInteger;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.worker.JobFactory;
import net.greghaines.jesque.worker.Worker;
import net.greghaines.jesque.worker.WorkerPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Parameters(commandDescription = "Start worker(s) to execute jobs")
public class WorkCommand extends Command {

    @Parameter(description = "queue ...", required = true, variableArity = true)
    private List<String> sources = new ArrayList<String>();

    @Parameter(names = {"-cp", "--classpath", "--class-paths"}, description = "The class paths to load job classes from", variableArity = true)
    private List<String> classpaths = new ArrayList<String>();

    @Parameter(names = {"-p", "--packages"}, description = "The packages to look into for job classes", variableArity = true)
    private List<String> packages = new ArrayList<String>(Collections.singletonList(""));

    @Parameter(names = {"-n", "--num-workers"}, description = "The number of workers in the pool", validateWith = PositiveInteger.class)
    private int numWorkers = 1;

    @Override
    protected void execute(Config config) throws Exception {

        ClassLoader classLoader = new PathsClassLoader(classpaths);

        JobFactory jobFactory = new MultiPackageJobFactory(classLoader, packages);

        Callable<Worker> workerFactory = new WorkerFactory(config, sources, jobFactory);

        WorkerPool workerPool = new WorkerPool(workerFactory, numWorkers);

        Runtime.getRuntime().addShutdownHook(new Thread(new CleanWorkerPoolShutdown(workerPool)));

        workerPool.run();
    }
}
