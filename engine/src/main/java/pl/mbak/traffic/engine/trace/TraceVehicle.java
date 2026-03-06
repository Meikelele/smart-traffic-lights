package pl.mbak.traffic.engine.trace;

import pl.mbak.traffic.engine.domain.Turn;

public record TraceVehicle(
        String vehicleId,
        Turn turn
) {}
