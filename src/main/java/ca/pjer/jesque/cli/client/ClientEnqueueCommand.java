package ca.pjer.jesque.cli.client;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.greghaines.jesque.Config;
import net.greghaines.jesque.Job;
import net.greghaines.jesque.client.Client;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Parameters(commandDescription = "Enqueue a job")
public class ClientEnqueueCommand extends ClientCommand {

    @Parameter(description = "queue job", required = true, variableArity = false, arity = 2)
    private List<String> destination = new ArrayList<String>();

    @Parameter(names = {"-a", "--arg", "--argument"}, description = "The arguments of the job", variableArity = true)
    private List<String> arguments = new ArrayList<String>();

    @Parameter(names = {"-ja", "--json-args", "--json-arguments"}, description = "The arguments of the job in JSON (must be a JSON array)")
    private String argumentsJSON;

    @DynamicParameter(names = {"-v", "--var", "--variable"}, description = "The variables of the job")
    private Map<String, String> variables = new LinkedHashMap<String, String>();

    @Parameter(names = {"-jv", "--json-vars", "--json-variables"}, description = "The variables of the job in JSON (must be a JSON object)")
    private String variablesJSON;

    @Parameter(names = {"-p", "--priority"}, description = "Execute job ASAP")
    private boolean priority = false;

    @Override
    protected void execute(Config config, Client client) throws Exception {
        if (destination.size() != 2) {
            throw new Exception("Invalid destination (queue and job)");
        }
        Job job = new Job(destination.get(1));
        if (!arguments.isEmpty() && argumentsJSON != null) {
            throw new Exception("Can not specify both -a and -ja");
        }
        if (!variables.isEmpty() && variablesJSON != null) {
            throw new Exception("Can not specify both -v and -jv");
        }
        if (argumentsJSON != null) {
            job.setArgs(new ObjectMapper().readValue(argumentsJSON, Object[].class));
        } else if (!arguments.isEmpty()) {
            job.setArgs(arguments.toArray());
        }
        if (!variables.isEmpty()) {
            job.setVars(variables);
        } else if (variablesJSON != null) {
            //noinspection unchecked
            job.setVars(new ObjectMapper().readValue(variablesJSON, Map.class));
        }
        if (priority)
            client.priorityEnqueue(destination.get(0), job);
        else
            client.enqueue(destination.get(0), job);
    }
}
