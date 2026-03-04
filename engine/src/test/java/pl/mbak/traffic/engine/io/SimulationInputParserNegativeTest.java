package pl.mbak.traffic.engine.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationInputParserNegativeTest {

    @Test
    void shouldFailOnUnknownCommandType() throws Exception {
        String json = """
                {
                  "commands": [
                    { "type": "unknownType", "foo": "bar" }
                  ]
                }
                """;

        ObjectMapper mapper = JsonMapper.mapper();

        assertThrows(Exception.class, () -> mapper.readValue(json, SimulationInput.class));
    }
}