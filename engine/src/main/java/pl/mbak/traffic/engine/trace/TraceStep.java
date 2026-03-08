package pl.mbak.traffic.engine.trace;

import pl.mbak.traffic.engine.domain.Road;
import pl.mbak.traffic.engine.simulation.Phase;

import java.util.List;
import java.util.Map;

public record TraceStep(
        String command,
        Phase phase,
        Map<Road, List<TraceVehicle>> queues,
        List<String> leftVehicles

) { }
