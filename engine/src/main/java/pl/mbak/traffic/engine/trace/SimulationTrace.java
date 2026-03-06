package pl.mbak.traffic.engine.trace;

import pl.mbak.traffic.engine.io.SimulationOutput;

import java.util.List;

public record SimulationTrace(
        List<TraceStep> steps,
        SimulationOutput simulationOutput
) {
}
