package pl.mbak.traffic.engine.domain;

public record Vehicle(String vehicleId, Road startRoad, Road endRoad, Turn turn) {
}
