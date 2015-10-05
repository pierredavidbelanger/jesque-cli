package ca.pjer.jesque.cli.client;

import ca.pjer.jesque.cli.Command;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.client.Client;
import net.greghaines.jesque.client.ClientImpl;

public abstract class ClientCommand extends Command {

    @Override
    protected void execute(Config config) throws Exception {
        Client client = new ClientImpl(config, true);
        try {
            execute(config, client);
        } finally {
            client.end();
        }
    }

    protected abstract void execute(Config config, Client client) throws Exception;

}
