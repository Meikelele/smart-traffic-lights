export type Road = 'north' | 'south'| 'west' | 'east';
export type Turn = 'LEFT' | 'RIGHT'| 'STRAIGHT';
export type Phase = 'NS_THROUGH' | 'NS_LEFT' | 'EW_THROUGH' | 'EW_LEFT';

export interface TraceVehicle {
  vehicleId: string;
  turn: Turn;
}

export interface TraceStep {
  command: string;
  phase: Phase | null;
  queues: Record<Road, TraceVehicle[]>;
  leftVehicles: string[];
}

export interface StepStatus {
  leftVehicles: string[];
}

export interface SimulationOutput {
  stepStatuses: StepStatus[];
}

export interface SimulationTrace {
  steps: TraceStep[];
  simulationOutput: SimulationOutput;
}

export interface AddVehicleCommand {
  type: 'addVehicle';
  vehicleId: string;
  startRoad: Road;
  endRoad: Road;
}

export interface StepCommand {
  type: 'step';
}

export type Command = AddVehicleCommand | StepCommand;

export interface SimulationInput {
  commands: Command[];
}
