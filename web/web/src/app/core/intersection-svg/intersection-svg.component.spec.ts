
// describe - zgrupowane testy, opisujesz czego dotycza testy
import {IntersectionSvgComponent} from './intersection-svg.component';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {Phase, Road, TraceStep, TraceVehicle} from '../models/simulation.models';

describe("IntersectionSvgComponent", () => {
  // instancja klasy czyli komponentu, zeby wywolywac metody
  let component: IntersectionSvgComponent;

  // całe srodowisko komponentu
  let fixture: ComponentFixture<IntersectionSvgComponent>;

  beforeEach( async () => {
    // bede testowal ten komponent, jako ze standalone to musze wrzucic import, kompilacja
    await TestBed.configureTestingModule({
      imports: [IntersectionSvgComponent]
    }).compileComponents();

    // stworzenie instacji komponentu
    fixture = TestBed.createComponent(IntersectionSvgComponent);

    // obiekt klasy
    component = fixture.componentInstance;

    // wyrenderuj komponent
    fixture.detectChanges();

  })

  it("should create IntersectionSvgComponent component", () => {
    expect(component).toBeTruthy();
  });

  it("should return L for LEFT turn", () => {
    expect(component.turnLabel('LEFT')).toBe("L");
  });

  it("should return R for RIGHT turn", () => {
    expect(component.turnLabel('RIGHT')).toBe("R");
  });

  it("should return S for STRAIGHT 'turn'", () => {
    expect(component.turnLabel("STRAIGHT")).toBe("S");
  });

  it("should shorten vehicle id by removing 'vehicle' part prefix", () => {
    expect(component.shortVehicleId("vehicle12")).toBe("12");
  });

  it("should shorten vehicle id by removing 'vehicle' part prefix also", () => {
    expect(component.shortVehicleId("vehicle1")).toBe("1");
  });

  it("should keep original id if it doesn't start with 'vehicle'", () => {
    expect(component.shortVehicleId("ligthning67")).toBe("ligthning67");
  });

  function createStep(phase: TraceStep['phase']): TraceStep {
    return {
      command: 'step',
      phase,
      queues: {
        north: [],
        south: [],
        west: [],
        east: []
      },
      leftVehicles: []
      };
    }

  it("should detect NS phase as active for NS_THROUGH", () => {
    component.step = createStep("NS_THROUGH");
    expect(component.isNsActive()).toBeTrue();
    expect(component.isEwActive()).toBeFalse();
  });

  it("should return false for both phase helpers when phase is null", () => {
    component.step = {
      command: 'addVehicle',
      phase: null,
      queues: {
        north: [],
        south: [],
        east: [],
        west: []
      },
      leftVehicles: []
    };
    expect(component.isEwActive()).toBeFalse();
    expect(component.isNsActive()).toBeFalse();
  })

  it("should expose north queue vehicles from step input", () => {
    component.step = {
      command: 'step',
      phase: 'NS_THROUGH',
      queues: {
        north: [{ vehicleId: 'vehicle1', turn: 'STRAIGHT' }],
        south: [],
        east: [],
        west: []
      },
      leftVehicles: []
    };

    expect(component.northVehicles.length).toBe(1);
    expect(component.northVehicles[0].vehicleId).toBe("vehicle1");
    expect(component.northVehicles[0].turn).toBe("STRAIGHT");
  })

})
