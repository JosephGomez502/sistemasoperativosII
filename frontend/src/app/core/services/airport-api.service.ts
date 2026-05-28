import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Aircraft, Airport, Dashboard, Flight, Reservation, Seat } from './api.types';

@Injectable({ providedIn: 'root' })
export class AirportApiService {
  private api = environment.apiUrl;
  constructor(private http: HttpClient) {}
  flights(origin = '', destination = '') { return this.http.get<Flight[]>(`${this.api}/public/flights`, { params: { origin, destination } }); }
  seats(flightId: number) { return this.http.get<Seat[]>(`${this.api}/public/flights/${flightId}/seats`); }
  checkout(body: unknown) { return this.http.post<Reservation>(`${this.api}/client/checkout`, body); }
  reservations() { return this.http.get<Reservation[]>(`${this.api}/client/reservations`); }
  profile() { return this.http.get<Record<string, string>>(`${this.api}/client/profile`); }
  updateProfile(body: unknown) { return this.http.put<Record<string, string>>(`${this.api}/client/profile`, body); }
  dashboard() { return this.http.get<Dashboard>(`${this.api}/admin/dashboard`); }
  adminAirports() { return this.http.get<Airport[]>(`${this.api}/admin/airports`); }
  saveAirport(body: Partial<Airport>) { return this.http.post<Airport>(`${this.api}/admin/airports`, body); }
  adminAircraft() { return this.http.get<Aircraft[]>(`${this.api}/admin/aircraft`); }
  saveAircraft(body: Partial<Aircraft>) { return this.http.post<Aircraft>(`${this.api}/admin/aircraft`, body); }
  adminFlights() { return this.http.get<Flight[]>(`${this.api}/admin/flights`); }
  saveFlight(body: unknown) { return this.http.post<Flight>(`${this.api}/admin/flights`, body); }
  ticketUrl(code: string) { return `${this.api}/client/reservations/${code}/ticket.pdf`; }
}
