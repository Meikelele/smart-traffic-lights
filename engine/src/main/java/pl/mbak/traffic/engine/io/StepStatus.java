package pl.mbak.traffic.engine.io;

import java.util.List;

public record StepStatus(List<String> leftVehicles) {

    public StepStatus {
        leftVehicles = (leftVehicles == null) ? List.of() : List.copyOf(leftVehicles);
    }

    public static StepStatus empty() {
        return new StepStatus(List.of());
    }
}
