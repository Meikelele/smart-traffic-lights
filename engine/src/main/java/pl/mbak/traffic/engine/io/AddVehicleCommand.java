package pl.mbak.traffic.engine.io;

import pl.mbak.traffic.engine.domain.Road;

public record AddVehicleCommand(
        String vehicleId,
        Road startRoad,
        Road endRoad
) implements Command {
}
