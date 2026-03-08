import {AppComponent} from './app.component';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SimulationApiService} from './core/services/simulation-api.service';
import {SimulationTrace} from './core/models/simulation.models';
import {of} from 'rxjs';


describe("AppComponent", () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;

  let simulationApiServiceSpy: jasmine.SpyObj<SimulationApiService>;

  const mockTrace: SimulationTrace = {
    steps: [
      {
        command: 'addVehicle',
        phase: null,
        queues: {
          north: [],
          south: [{ vehicleId: 'vehicle1', turn: 'STRAIGHT' }],
          east: [],
          west: []
        },
        leftVehicles: []
      },
      {
        command: 'step',
        phase: 'NS_THROUGH',
        queues: {
          north: [],
          south: [],
          east: [],
          west: []
        },
        leftVehicles: ['vehicle1']
      }
    ],
    simulationOutput: {
      stepStatuses: [
        {
          leftVehicles: ['vehicle1']
        }
      ]
    }
  };

  beforeEach( async () => {
    simulationApiServiceSpy = jasmine.createSpyObj('SimulationApiService', ['simulateTrace']);

    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [{
        provide: SimulationApiService,
        useValue: simulationApiServiceSpy
      }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sample input', () => {
    component.loadSample();

    expect(component.loadedFileName).toBe('input_sample.json');
    expect(component.loadedJsonText).toContain('"commands"');
    expect(component.errorMessage).toBe('');
  });

  it('should reset app state', () => {
    component.trace = mockTrace;
    component.errorMessage = 'Some error -_-';
    component.loadedJsonText = '{"commands:[]"}';
    component.loadedFileName = 'test.json';
    component.currentStepIndex = 1;
    component.isLoading = true;

    component.reset();

    expect(component.trace).toBeNull();
    expect(component.errorMessage).toBe('');
    expect(component.loadedJsonText).toBe('');
    expect(component.loadedFileName).toBe('');
    expect(component.currentStepIndex).toBe(0);
    expect(component.isLoading).toBeFalse();
  });

  it('should set error when trying to star simulation with empty input', () => {
    component.loadedJsonText = '';

    component.startSimulation();

    expect(component.errorMessage).toBe('Please upload a JSON file or load the sample first.');
    expect(simulationApiServiceSpy.simulateTrace).not.toHaveBeenCalled();
  });

  it('should expose current step basend on currentStepIndex', () => {
    component.trace = mockTrace;
    component.currentStepIndex = 1;

    expect(component.currentStep).toEqual(mockTrace.steps[1]);
  });

  it('should call API and store trace when simulation starts successefully', () => {
    component.loadedJsonText = JSON.stringify({
      commands: [
        {
          type: 'addVehicle',
          vehicleId: 'vehicle1',
          startRoad: 'south',
          endRoad: 'north'
        },
        {
          type: 'step'
        }
      ]
    });

    simulationApiServiceSpy.simulateTrace.and.returnValue(of(mockTrace));

    const startPlaybackSpy = spyOn(component as any, 'startPlayback');
    component.startSimulation();

    expect(simulationApiServiceSpy.simulateTrace).toHaveBeenCalled();
    expect(component.trace).toEqual(mockTrace);
    expect(component.currentStepIndex).toBe(0);
    expect(component.isLoading).toBeFalse();
    expect(startPlaybackSpy).toHaveBeenCalled();
  });
});
