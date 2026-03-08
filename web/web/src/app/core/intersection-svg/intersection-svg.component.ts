import {Component, Input, input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TraceStep, TraceVehicle} from '../models/simulation.models';

@Component({
  selector: 'app-intersection-svg',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './intersection-svg.component.html',
  styleUrl: './intersection-svg.component.scss'
})
export class IntersectionSvgComponent {
  @Input() step: TraceStep | null = null;

  get northVehicles(): TraceVehicle[] {
    return this.step?.queues.north ?? [];
  }

  get southVehicles(): TraceVehicle[] {
    return this.step?.queues.south ?? [];
  }

  get eastVehicles(): TraceVehicle[] {
    return this.step?.queues.east ?? [];
  }

  get westVehicles(): TraceVehicle[] {
    return this.step?.queues.west ?? [];
  }
  isNsActive(): boolean {
    return this.step?.phase === 'NS_THROUGH' || this.step?.phase === 'NS_LEFT';
  }

  isEwActive(): boolean {
    return this.step?.phase === 'EW_THROUGH' || this.step?.phase === 'EW_LEFT';
  }

  turnLabel(turn: string): string {
    switch (turn) {
      case 'LEFT':
        return 'L';
      case 'RIGHT':
        return 'R';
      case 'STRAIGHT':
        return 'S';
      default:
        return '?';
    }
  }

  shortVehicleId(vehicleId: string): string {
    return vehicleId.replace(/^vehicle/i, '');
  }
}
