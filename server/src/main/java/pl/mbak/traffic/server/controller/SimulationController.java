package pl.mbak.traffic.server.controller;

import org.springframework.web.bind.annotation.*;
import pl.mbak.traffic.engine.io.SimulationInput;
import pl.mbak.traffic.engine.simulation.SimulationEngine;
import pl.mbak.traffic.engine.trace.SimulationTrace;

@RestController
@RequestMapping("/simulate")
public class SimulationController {

    private final SimulationEngine engine = new SimulationEngine();


    @PostMapping("/trace")
    public SimulationTrace simulationTrace(@RequestBody SimulationInput input) {
        return engine.simulateWithTrace(input);
    }
}
