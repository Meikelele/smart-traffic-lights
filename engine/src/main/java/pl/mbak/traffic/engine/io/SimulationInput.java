package pl.mbak.traffic.engine.io;

import java.util.List;

public record SimulationInput(List<Command> commands) {
    public SimulationInput {
        commands = (commands == null) ? List.of() : List.copyOf(commands);
    }
}
