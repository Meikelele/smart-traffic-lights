package pl.mbak.traffic.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import pl.mbak.traffic.engine.io.JsonMapper;
import pl.mbak.traffic.engine.io.SimulationInput;
import pl.mbak.traffic.engine.io.SimulationOutput;
import pl.mbak.traffic.engine.simulation.SimulationEngine;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Please use formula: java -jar traffic-cli.jar <input.json> <output.json>");
            System.exit(2);
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        if (!Files.exists(inputPath)) {
            System.err.println("Input file does not exists: " + inputPath.toAbsolutePath());
            System.exit(2);
        }

        ObjectMapper mapper = JsonMapper.mapper();
        SimulationInput input = mapper.readValue(Files.readString(inputPath), SimulationInput.class);
        SimulationEngine engine = new SimulationEngine();
        SimulationOutput output = engine.simulate(input);

        String outputJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
        Files.writeString(outputPath, outputJson);

        System.out.println("[I'm DONE]: " + outputPath.toAbsolutePath());
    }


}
