package pl.mbak.traffic.engine.simulation;

import org.junit.jupiter.api.Test;
import pl.mbak.traffic.engine.domain.Road;
import pl.mbak.traffic.engine.io.AddVehicleCommand;
import pl.mbak.traffic.engine.io.SimulationInput;
import pl.mbak.traffic.engine.io.SimulationOutput;
import pl.mbak.traffic.engine.io.StepCommand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimulationFifoBlockingTest {

    @Test
    void leftAtHeadShouldBlockThroughOnSameRoad() {
        SimulationInput input = new SimulationInput(List.of(
                new AddVehicleCommand("e_left", Road.east, Road.south),
                new AddVehicleCommand("e_straight", Road.east, Road.west),
                new AddVehicleCommand("w_straight", Road.west, Road.east),
                new StepCommand()
        ));

        SimulationEngine engine = new SimulationEngine();
        SimulationOutput out = engine.simulate(input);

        assertEquals(1, out.stepStatuses().size());
        List<String> left = out.stepStatuses().get(0).leftVehicles();

        assertTrue(left.contains("w_straight"), "west straight should pass");
        assertFalse(left.contains("e_straight"), "east straight must NOT pass because LEFT is at head");
        assertFalse(left.contains("e_left"), "east left must NOT pass in THROUGH phase");
    }
}