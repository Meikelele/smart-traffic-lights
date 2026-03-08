import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SimulationInput, SimulationTrace} from '../models/simulation.models';
import {Observable} from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class SimulationApiService {
  private readonly http = inject(HttpClient);

  simulateTrace(input: SimulationInput): Observable<SimulationTrace> {
    return this.http.post<SimulationTrace>(`http://localhost:8080/simulate/trace`, input);
  }
}
