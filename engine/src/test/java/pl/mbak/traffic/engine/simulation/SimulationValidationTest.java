package pl.mbak.traffic.engine.simulation;

import org.junit.jupiter.api.Test;
import pl.mbak.traffic.engine.domain.Road;
import pl.mbak.traffic.engine.io.AddVehicleCommand;
import pl.mbak.traffic.engine.io.SimulationInput;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulationValidationTest {

    @Test
    void shouldRejectDuplicateVehicleId() {
        SimulationInput input = new SimulationInput(List.of(
                new AddVehicleCommand("vehicle1", Road.south, Road.north),
                new AddVehicleCommand("vehicle1", Road.west, Road.east)
        ));

        SimulationEngine engine = new SimulationEngine();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> engine.simulate(input));
        assertTrue(ex.getMessage().toLowerCase().contains("duplicate"), "Expected duplicate message, got: " + ex.getMessage());
    }
}
