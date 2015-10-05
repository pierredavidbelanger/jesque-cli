package ca.pjer.jesque.cli.worker;

import net.greghaines.jesque.Job;
import net.greghaines.jesque.utils.ReflectionUtils;
import net.greghaines.jesque.worker.JobFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MultiPackageJobFactory implements JobFactory {

    protected final ClassLoader classLoader;
    protected final List<String> packages;
    protected final Map<String, String> jobClassNameCache;

    public MultiPackageJobFactory(ClassLoader classLoader, List<String> packages) {
        this.classLoader = classLoader;
        this.packages = packages;
        jobClassNameCache = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public Object materializeJob(Job job) throws Exception {
        String jobName = job.getClassName();
        Class jobClass = null;
        String jobClassName = jobClassNameCache.get(jobName);
        if (jobClassName != null) {
            jobClass = ReflectionUtils.forName(jobClassName, classLoader);
        } else {
            for (String jobPackage : packages) {
                jobClassName = jobPackage.isEmpty() ? jobName : jobPackage + "." + jobName;
                try {
                    jobClass = ReflectionUtils.forName(jobClassName, classLoader);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
                if (jobClass != null) {
                    jobClassNameCache.put(jobName, jobClassName);
                    break;
                }
            }
            if (jobClass == null) {
                throw new ClassNotFoundException(jobName);
            }
        }
        return ReflectionUtils.createObject(jobClass, job.getArgs(), job.getVars());
    }
}
