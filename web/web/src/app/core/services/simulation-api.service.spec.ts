import {SimulationApiService} from './simulation-api.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {SimulationInput, SimulationTrace} from '../models/simulation.models';
import {input} from '@angular/core';


describe("SimulationApiService", () => {
  let service: SimulationApiService;
  let httpTestingController: HttpTestingController;

  // srodowisko testowe dla serwisu
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SimulationApiService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    // testowa instancja serwisu
    service = TestBed.inject(SimulationApiService);
    // kontroler do przechwytywania requestow
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  // sprawdz po kazdym tescie czy nie zostaly jakies nieobsluzone requesty
  afterEach(() => {
    httpTestingController.verify();
  });


  it("should send POST req to /simulate/trace and return simulation trace", () => {
    const input: SimulationInput = {
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
    };

    const mockResponse: SimulationTrace = {
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

    let actualResponse: SimulationTrace | undefined;

    service.simulateTrace(input).subscribe((res) => {
      actualResponse = res;
    });

    const req = httpTestingController.expectOne(
      'http://localhost:8080/simulate/trace'
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(input);
    req.flush(mockResponse);
    expect(actualResponse).toEqual(mockResponse);

  });
});
