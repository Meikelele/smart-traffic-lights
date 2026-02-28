package pl.mbak.traffic.engine.io;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pl.mbak.traffic.engine.simulation.SimulationEngine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationGoldenTest {
    @Test
    @Disabled("Pending: implement SimulationEngine logic")
    void shouldMatchExpectedOutputForSample() throws Exception {
        Path inputPath = Path.of("../samples/input_sample.json");
        Path expectedPath = Path.of("../samples/output_sample.json");

        assertTrue(Files.exists(inputPath), "Missing: " + inputPath.toAbsolutePath());
        assertTrue(Files.exists(expectedPath), "Missing: " + expectedPath.toAbsolutePath());

        ObjectMapper mapper = JsonMapper.mapper();

        SimulationInput input = mapper.readValue(Files.readString(inputPath), SimulationInput.class);
        SimulationOutput expected = mapper.readValue(Files.readString(expectedPath), SimulationOutput.class);

        SimulationEngine engine = new SimulationEngine();
        SimulationOutput actual = engine.simulate(input);

        assertEquals(expected, actual);
    }
}
