package ca.pjer.jesque.cli;

import ca.pjer.jesque.cli.client.ClientEnqueueCommand;
import ca.pjer.jesque.cli.worker.WorkCommand;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters
public class Main {

    @Parameter(names = {"-h", "--help"}, description = "Print this help and quit", help = true)
    private boolean help = false;

    protected void execute(String[] args) throws Exception {

        JCommander jCommander = new JCommander(this);

        jCommander.addCommand("enqueue", new ClientEnqueueCommand());
        jCommander.addCommand("work", new WorkCommand());

        String command;
        try {
            jCommander.parse(args);
            command = jCommander.getParsedCommand();
            if (help) {
                jCommander.usage();
                return;
            }
            if (command == null) throw new Exception("No command specified");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            jCommander.usage();
            return;
        }

        Command abstractCommand = (Command) jCommander.getCommands().get(command).getObjects().get(0);
        abstractCommand.execute();
    }

    public static void main(String[] args) throws Exception {
        new Main().execute(args);
    }
}
