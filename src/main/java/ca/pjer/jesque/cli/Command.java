package ca.pjer.jesque.cli;

import com.beust.jcommander.Parameter;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.ConfigBuilder;

import java.net.URI;

public abstract class Command {

    @Parameter(names = {"-r", "--redis-url"}, description = "The Redis connection URL")
    protected String redisUrl = "redis://localhost:6379";

    public void execute() throws Exception {
        URI redisUri = new URI(redisUrl);
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.withHost(redisUri.getHost());
        configBuilder.withPort(redisUri.getPort());
        if (redisUri.getUserInfo() != null)
            configBuilder.withPassword(redisUri.getUserInfo().split(":", 2)[1]);
        Config config = configBuilder.build();
        execute(config);
    }

    protected abstract void execute(Config config) throws Exception;

}
