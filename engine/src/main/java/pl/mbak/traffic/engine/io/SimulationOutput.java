package pl.mbak.traffic.engine.io;

import java.util.List;

public record SimulationOutput(List<StepStatus> stepStatuses) {
    public SimulationOutput {
        stepStatuses = (stepStatuses == null) ? List.of() : List.copyOf(stepStatuses);
    }

    public static SimulationOutput empty() {
        return new SimulationOutput(List.of());
    }

}
