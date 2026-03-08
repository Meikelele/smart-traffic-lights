package pl.mbak.traffic.engine.simulation;

import pl.mbak.traffic.engine.domain.*;
import pl.mbak.traffic.engine.io.*;
import pl.mbak.traffic.engine.trace.*;

import java.util.*;

public final class SimulationEngine {

    public SimulationOutput simulate(SimulationInput input) {

        // FIFO for every road
        EnumMap<Road, ArrayDeque<Vehicle>> queues = new EnumMap<>(Road.class);
        for (Road road : Road.values()) {
            queues.put(road, new ArrayDeque<>());
        }

        Set<String> knownVehicleIds = new HashSet<>();
        List<StepStatus> stepStatuses = new ArrayList<>();

        for (Command command : input.commands()) {
            // adding car to queue
            if (command instanceof AddVehicleCommand addVehicleCommand) {
                validateAddVehicle(addVehicleCommand, knownVehicleIds);
                Turn turn = Turns.compute(addVehicleCommand.startRoad(), addVehicleCommand.endRoad());
                queues.get(addVehicleCommand.startRoad())
                        .addLast(new Vehicle(
                                addVehicleCommand.vehicleId(),
                                addVehicleCommand.startRoad(),
                                addVehicleCommand.endRoad(),
                                turn)
                        );
            }
            else if (command instanceof StepCommand stepCommand) {
                Phase phase = choosePhase(queues);
                List<String> left = performStep(queues, phase);
                stepStatuses.add(new StepStatus(left));
            }
            else {
                throw new IllegalArgumentException("Unknown command type: " + command.getClass());
            }
        }

        return new SimulationOutput(stepStatuses);
    }

    public SimulationTrace simulateWithTrace(SimulationInput input) {
        // TODO: ref related to with DRY -simulate
        EnumMap<Road, ArrayDeque<Vehicle>> queues = new EnumMap<>(Road.class);
        for (Road road : Road.values()) {
            queues.put(road, new ArrayDeque<>());
        }

        Set<String> knownVehicleIds = new HashSet<>();
        List<StepStatus> stepStatuses = new ArrayList<>();
        List<TraceStep> traceSteps = new ArrayList<>();

        for (Command command : input.commands()) {
            if (command instanceof AddVehicleCommand addVehicleCommand) {
                validateAddVehicle(addVehicleCommand, knownVehicleIds);

                Turn turn = Turns.compute(addVehicleCommand.startRoad(), addVehicleCommand.endRoad());
                queues.get(addVehicleCommand.startRoad())
                        .addLast(new Vehicle(
                                addVehicleCommand.vehicleId(),
                                addVehicleCommand.startRoad(),
                                addVehicleCommand.endRoad(),
                                turn
                        ));

                traceSteps.add(new TraceStep(
                        "addVehicle",
                        null,
                        snapshotQueues(queues),
                        List.of()
                ));
            }
            else if (command instanceof StepCommand) {
                Phase phase = choosePhase(queues);
                List<String> leftVehicles = performStep(queues, phase);
                stepStatuses.add(new StepStatus(leftVehicles));

                traceSteps.add(new TraceStep(
                        "step",
                        phase,
                        snapshotQueues(queues),
                        List.copyOf(leftVehicles)
                ));
            }
            else {
                throw new IllegalArgumentException("Unknown command type: " + command.getClass());
            }
        }

        return new SimulationTrace(
                List.copyOf(traceSteps),
                new SimulationOutput(stepStatuses)
        );
    }

    private Map<Road, List<TraceVehicle>> snapshotQueues(EnumMap<Road, ArrayDeque<Vehicle>> queues) {
        EnumMap<Road, List<TraceVehicle>> snapshot = new EnumMap<>(Road.class);

        for (Road road : Road.values()) {
            List<TraceVehicle> vehicleIds = queues.get(road)
                    .stream()
                    .map(vehicle -> new TraceVehicle(vehicle.vehicleId(), vehicle.turn()))
                    .toList();

            snapshot.put(road, vehicleIds);
        }

        return snapshot;
    }

    private static List<String> performStep(EnumMap<Road, ArrayDeque<Vehicle>> queues, Phase phase) {
        List<String> leftVehicles = new ArrayList<>(2);

        switch (phase) {
            case NS_THROUGH -> {
                tryPopIfEligible(queues, Road.south, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT), leftVehicles);
                tryPopIfEligible(queues, Road.north, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT), leftVehicles);
            }
            case NS_LEFT -> {
                tryPopIfEligible(queues, Road.north, EnumSet.of(Turn.LEFT), leftVehicles);
                tryPopIfEligible(queues, Road.south, EnumSet.of(Turn.LEFT), leftVehicles);
            }
            case EW_THROUGH -> {
                tryPopIfEligible(queues, Road.east, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT), leftVehicles);
                tryPopIfEligible(queues, Road.west, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT), leftVehicles);
            }
            case EW_LEFT -> {
                tryPopIfEligible(queues, Road.east, EnumSet.of(Turn.LEFT), leftVehicles);
                tryPopIfEligible(queues, Road.west, EnumSet.of(Turn.LEFT), leftVehicles);
            }
        }

        return leftVehicles;
    }

    private static void tryPopIfEligible(EnumMap<Road, ArrayDeque<Vehicle>> queues, Road road, EnumSet<Turn> allowedTurns, List<String> leftVehicle) {
        ArrayDeque<Vehicle> roadQueue = queues.get(road);
        Vehicle headVehicle = roadQueue.peekFirst();
        if (headVehicle == null) {
            return;
        }

        if (allowedTurns.contains(headVehicle.turn())) {
            Vehicle vehicleToLeft = roadQueue.pollFirst();
            leftVehicle.add(vehicleToLeft.vehicleId());
        }
    }

    private static Phase choosePhase(EnumMap<Road, ArrayDeque<Vehicle>> queues) {
        int nsThrough = scoreRoad(queues, Road.north, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT)) +
                        scoreRoad(queues, Road.south, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT));
        int nsLeft = scoreRoad(queues, Road.north, EnumSet.of(Turn.LEFT)) +
                     scoreRoad(queues, Road.south, EnumSet.of(Turn.LEFT));
        int ewThrough = scoreRoad(queues, Road.east, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT)) +
                        scoreRoad(queues, Road.west, EnumSet.of(Turn.STRAIGHT, Turn.RIGHT));
        int ewLeft = scoreRoad(queues, Road.east, EnumSet.of(Turn.LEFT)) +
                     scoreRoad(queues, Road.west, EnumSet.of(Turn.LEFT));

        return argmaxWithTieBreak(
                Map.of(
                    Phase.NS_THROUGH, nsThrough,
                    Phase.NS_LEFT, nsLeft,
                    Phase.EW_THROUGH, ewThrough,
                    Phase.EW_LEFT, ewLeft
                ),
                List.of(Phase.NS_THROUGH, Phase.NS_LEFT, Phase.EW_THROUGH, Phase.EW_LEFT)
        );
    }

    private static Phase argmaxWithTieBreak(Map<Phase, Integer> scores, List<Phase> tieBreakOrder) {
        int best = Integer.MIN_VALUE;

        for (Phase phase : tieBreakOrder) {
            best = Math.max(best, scores.getOrDefault(phase, 0));
        }

        for (Phase phase : tieBreakOrder) {
            if (scores.getOrDefault(phase, 0) == best) {
                return phase;
            }
        }

        return tieBreakOrder.get(0);
    }

    private static int scoreRoad(EnumMap<Road, ArrayDeque<Vehicle>> queues, Road road, EnumSet<Turn> allowed) {
        Vehicle headVehicle = queues.get(road).peekFirst();
        if (headVehicle == null) {
            return 0;
        }
        return allowed.contains(headVehicle.turn()) ? 1 : 0;
    }

    private static void validateAddVehicle(AddVehicleCommand addVehicleCommand, Set<String> knownVehicleIds) {
        if (addVehicleCommand.vehicleId() == null || addVehicleCommand.vehicleId().isBlank()) {
            throw new IllegalArgumentException("(1) 'vehicleId' must be provided");
        }
        if (addVehicleCommand.startRoad() == null || addVehicleCommand.endRoad() == null) {
            throw new IllegalArgumentException("(2) 'startRoad' and 'endRoad' must be provided");
        }
        if (addVehicleCommand.startRoad() == addVehicleCommand.endRoad()) {
            throw new IllegalArgumentException("(3) 'startRoad' must be different than 'endRoad'");
        }
        if (!knownVehicleIds.add(addVehicleCommand.vehicleId())) {
            throw new IllegalArgumentException("(4) Duplicate 'vehicleId': " + addVehicleCommand.vehicleId());
        }
    }


}
