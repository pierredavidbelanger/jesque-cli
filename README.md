# jesque-cli
A command-line interface for [Jesque](https://github.com/gresrun/jesque), an implementation of [Resque](https://github.com/resque/resque) in [Java](https://www.java.com).

## Usage

With the latest [jesque-cli-with-dependencies.jar](https://oss.sonatype.org/content/repositories/snapshots/ca/pjer/jesque-cli/0.1.0-SNAPSHOT/jesque-cli-0.1.0-20151005.022659-1-jar-with-dependencies.jar) snapshot:

```bash
$ java -jar jesque-cli-with-dependencies.jar --help
Usage: <main class> [options] [command] [command options]
  Options:
    -h, --help
       Print this help and quit
       Default: false
  Commands:
    enqueue      Enqueue a job
      Usage: enqueue [options] queue job
        Options:
          -a, --arg, --argument
             The arguments of the job
             Default: []
          -ja, --json-args, --json-arguments
             The arguments of the job in JSON (must be a JSON array)
          -jv, --json-vars, --json-variables
             The variables of the job in JSON (must be a JSON object)
          -p, --priority
             Execute job ASAP
             Default: false
          -r, --redis-url
             The Redis connection URL
             Default: redis://localhost:6379
          -v, --var, --variable
             The variables of the job
             Syntax: -vkey=value
             Default: {}

    work      Start worker(s) to execute jobs
      Usage: work [options] queue ...
        Options:
          -cp, --classpath, --class-paths
             The class paths to load job classes from
             Default: []
          -n, --num-workers
             The number of workers in the pool
             Default: 1
          -p, --packages
             The packages to look into for job classes
             Default: []
          -r, --redis-url
             The Redis connection URL
             Default: redis://localhost:6379
```

### enqueue

To enqueue `{"class":"TestAction","args":["a ctor arg"],"vars":{"prop":"a java bean property"}}` into `myqueue` (with a running Redis at `localhost:6379`):

```bash
$ java -jar jesque-cli-with-dependencies.jar enqueue \
    myqueue TestAction \
    -a 'a ctor arg' \
    -v prop='a java bean property'
```

### work

To run a worker that will run the above job from `myqueue` (with a running Redis at `localhost:6379`):

```bash
$ java -jar jesque-cli-with-dependencies.jar work \
    myqueue \
    -cp my-job.jar \
    -p com.acme.job
```

Where `my-job.jar` is a java jar package that contains the class `TestAction` into the `com.acme.job` package:

```java
package com.acme.job;

import java.util.concurrent.Callable;

public class TestAction implements Callable<String> {
    private final String arg;
    private String prop;

    public TestAction(String arg) { this.arg = "Hello World!"; }

    public void setProp(String prop) { this.prop = prop; }

    public String call() throws Exception {
        System.out.println(arg + " " + prop);
        return arg;
    }
}
```

## Integration with Maven

Adding this plugin into the `<project> <build> <plugins>` element of a `pom.xml` will fetch the latest latest [jesque-cli-with-dependencies.jar](https://oss.sonatype.org/content/repositories/snapshots/ca/pjer/jesque-cli/0.1.0-SNAPSHOT/jesque-cli-0.1.0-20151005.022659-1-jar-with-dependencies.jar) snapshot and copy it to `target/dependency/jesque-cli.jar`.

```xml
<plugin>
    <artifactId>maven-dependency-plugin</artifactId>
    <executions>
        <execution>
            <id>copy-dependency</id>
            <phase>package</phase>
            <goals>
                <goal>copy</goal>
            </goals>
            <configuration>
                <artifactItems>
                    <artifactItem>
                        <groupId>ca.pjer</groupId>
                        <artifactId>jesque-cli</artifactId>
                        <version>0.1.0-SNAPSHOT</version>
                        <classifier>jar-with-dependencies</classifier>
                        <destFileName>jesque-cli.jar</destFileName>
                    </artifactItem>
                </artifactItems>
            </configuration>
        </execution>
    </executions>
</plugin>
```

It will now be in a suitable place to be run with (for example) a `Procfile`:

```Procfile
web: java -jar target/my-app.jar
worker: java -jar target/dependency/jesque-cli.jar work myqueue -r $REDIS_URL -cp target/my-app.jar -p com.acme.job -n 2
```
