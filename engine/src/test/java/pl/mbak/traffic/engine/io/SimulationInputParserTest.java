package pl.mbak.traffic.engine.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SimulationInputParserTest {

    @Test
    void shouldParseSampleInput() throws Exception {
        Path sample = Path.of("../samples/input_sample.json");
        assertTrue(Files.exists(sample), "Sample file does not found: " + sample.toAbsolutePath());

        String json = Files.readString(sample);
        ObjectMapper mapper = JsonMapper.mapper();
        SimulationInput input = mapper.readValue(json, SimulationInput.class);

        assertNotNull(input);
        assertFalse(input.commands().isEmpty(), "Commands list should not be empty");
        assertTrue(input.commands().get(0) instanceof AddVehicleCommand);

        AddVehicleCommand firstCommand = (AddVehicleCommand) input.commands().get(0);
        assertEquals("vehicle1", firstCommand.vehicleId());
        assertEquals(pl.mbak.traffic.engine.domain.Road.south, firstCommand.startRoad());
        assertEquals(pl.mbak.traffic.engine.domain.Road.north, firstCommand.endRoad());
    }
}
