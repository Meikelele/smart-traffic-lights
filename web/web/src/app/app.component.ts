import {Component, inject, OnDestroy} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SimulationInput, SimulationTrace} from './core/models/simulation.models';
import {SimulationApiService} from './core/services/simulation-api.service';
import {IntersectionSvgComponent} from './core/intersection-svg/intersection-svg.component';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, IntersectionSvgComponent, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnDestroy{
  private readonly api = inject(SimulationApiService);

  title = 'Smart Traffic Ligths';
  trace: SimulationTrace | null = null;
  errorMessage = '';
  loadedFileName: string = '';
  loadedJsonText: string = '';
  isLoading: boolean = false;
  currentStepIndex : number = 0;
  isPlaying: boolean = false;
  delaysMs: number = 2000;

  private playbackTimer: ReturnType<typeof setInterval> | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.loadedFileName = file.name;

    const reader = new FileReader();
    reader.onload = () => {
      this.loadedJsonText = String(reader.result ?? '');
      this.errorMessage = '';
    };

    reader.onerror = () => {
      this.errorMessage = 'Failed to read the selected file -_-';
    };

    reader.readAsText(file);
  }

  loadSample(): void {
    const sample = {
      commands: [
        {
          type: 'addVehicle',
          vehicleId: 'vehicle1',
          startRoad: 'south',
          endRoad: 'north'
        },
        {
          type: 'addVehicle',
          vehicleId: 'vehicle2',
          startRoad: 'north',
          endRoad: 'south'
        },
        {
          type: 'step'
        },
        {
          type: 'step'
        },
        {
          type: 'addVehicle',
          vehicleId: 'vehicle3',
          startRoad: 'west',
          endRoad: 'south'
        },
        {
          type: 'addVehicle',
          vehicleId: 'vehicle4',
          startRoad: 'west',
          endRoad: 'south'
        },
        {
          type: 'addVehicle',
          vehicleId: 'vehicle5',
          startRoad: 'east',
          endRoad: 'south'
        },
        {
          type: 'step'
        },
        {
          type: 'step'
        },
        {
          type: 'step'
        }
      ]
    };

    this.loadedFileName = 'input_sample.json';
    this.loadedJsonText = JSON.stringify(sample, null, 2);
    this.errorMessage = '';
  }

  startSimulation(): void {
    this.errorMessage = '';
    this.trace = null;

    if (!this.loadedJsonText.trim()) {
      this.errorMessage = 'Please upload a JSON file or load the sample first.';
      return;
    }

    let parsed: SimulationInput;
    try {
      parsed = JSON.parse(this.loadedJsonText) as SimulationInput;

    } catch {
      this.errorMessage = 'Invalid JSON format';
      return;
    }

    this.isLoading = true;
    this.api.simulateTrace(parsed).subscribe({
      next: (res) => {
        this.trace = res;
        this.isLoading = false;
        this.currentStepIndex = 0;
        this.startPlayback();
      },
      error: () => {
        this.errorMessage = 'Failed to call backend API -_-';
        this.isLoading = false;
      }
    });
  }

  reset(): void {
    this.stopPlayback();
    this.currentStepIndex = 0;
    this.trace = null;
    this.errorMessage = '';
    this.loadedJsonText = '';
    this.loadedFileName = '';
    this.isLoading = false;
  }

  get currentStep() {
    return this.trace?.steps?.[this.currentStepIndex] ?? null;
  }


  startPlayback(): void {
    if (!this.trace || this.trace.steps.length === 0) {
      return;
    }

    this.stopPlayback();
    this.isPlaying = true;

    this.playbackTimer = setInterval(() => {
      if(!this.trace) {
        this.stopPlayback();
        return;
      }

      if (this.currentStepIndex >= this.trace.steps.length - 1) {
        this.stopPlayback();
        return;
      }
      this.currentStepIndex++;
    }, this.delaysMs);
  }

  pausePlayback(): void {
    this.stopPlayback();
  }

  nextStep(): void {
    if (!this.trace) {
      return;
    }

    if (this.currentStepIndex < this.trace.steps.length - 1) {
      this.currentStepIndex++;
    }
  }

  previousStep(): void {
    if (!this.trace) {
      return;
    }
    if (this.currentStepIndex > 0) {
      this.currentStepIndex--;
    }
  }

  private stopPlayback(): void {
    if (this.playbackTimer) {
      clearInterval(this.playbackTimer);
      this.playbackTimer = null;
    }
    this.isPlaying = false;
  }

  ngOnDestroy(): void {
    this.stopPlayback();
  }
}
