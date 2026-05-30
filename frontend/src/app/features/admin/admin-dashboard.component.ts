import { Component, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { AirportApiService } from '../../core/services/airport-api.service';
import { Aircraft, Airport, Customer, Dashboard, Flight, Payment, Reservation } from '../../core/services/api.types';

@Component({
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe, MatButtonModule, MatInputModule, MatSelectModule, MatTabsModule, MatTableModule],
  template: `
    <main class="page grid">
      <h1>CRM administrador TagAirlines</h1>
      @if (dashboard(); as d) {
        <section class="cards">
          <article class="surface metric"><strong>Ventas</strong><h2>{{d.sales | currency:'USD'}}</h2></article>
          <article class="surface metric"><strong>Vuelos activos</strong><h2>{{d.activeFlights}}</h2></article>
          <article class="surface metric"><strong>Clientes</strong><h2>{{d.users}}</h2></article>
          <article class="surface metric"><strong>Reservas</strong><h2>{{d.confirmedReservations}}</h2></article>
          <article class="surface metric"><strong>Aeropuertos</strong><h2>{{d.airports}}</h2></article>
          <article class="surface metric"><strong>Aviones</strong><h2>{{d.aircraft}}</h2></article>
        </section>
      }
      <mat-tab-group>
        <mat-tab label="CRM clientes">
          <section class="surface">
            <table mat-table [dataSource]="customers()">
              <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Cliente</th><td mat-cell *matCellDef="let c">{{c.fullName}}<br><small>{{c.email}}</small></td></ng-container>
              <ng-container matColumnDef="phone"><th mat-header-cell *matHeaderCellDef>Telefono</th><td mat-cell *matCellDef="let c">{{c.phone || 'N/A'}}</td></ng-container>
              <ng-container matColumnDef="doc"><th mat-header-cell *matHeaderCellDef>Documento</th><td mat-cell *matCellDef="let c">{{c.documentId || 'N/A'}}</td></ng-container>
              <ng-container matColumnDef="reservations"><th mat-header-cell *matHeaderCellDef>Reservas</th><td mat-cell *matCellDef="let c">{{c.reservations}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="customerCols"></tr><tr mat-row *matRowDef="let row; columns: customerCols;"></tr>
            </table>
          </section>
        </mat-tab>
        <mat-tab label="Reservas">
          <section class="surface">
            <table mat-table [dataSource]="reservations()">
              <ng-container matColumnDef="code"><th mat-header-cell *matHeaderCellDef>Codigo</th><td mat-cell *matCellDef="let r">{{r.code}}</td></ng-container>
              <ng-container matColumnDef="route"><th mat-header-cell *matHeaderCellDef>Ruta</th><td mat-cell *matCellDef="let r">{{r.flight.origin.iataCode}} - {{r.flight.destination.iataCode}}</td></ng-container>
              <ng-container matColumnDef="seat"><th mat-header-cell *matHeaderCellDef>Asiento</th><td mat-cell *matCellDef="let r">{{r.seat.seatNumber}}</td></ng-container>
              <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Estado</th><td mat-cell *matCellDef="let r">{{r.status}}</td></ng-container>
              <ng-container matColumnDef="email"><th mat-header-cell *matHeaderCellDef>Email</th><td mat-cell *matCellDef="let r">{{r.emailSent ? 'Enviado' : 'Pendiente'}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="reservationCols"></tr><tr mat-row *matRowDef="let row; columns: reservationCols;"></tr>
            </table>
          </section>
        </mat-tab>
        <mat-tab label="Pagos">
          <section class="surface">
            <table mat-table [dataSource]="payments()">
              <ng-container matColumnDef="reservation"><th mat-header-cell *matHeaderCellDef>Reserva</th><td mat-cell *matCellDef="let p">{{p.reservationCode}}</td></ng-container>
              <ng-container matColumnDef="amount"><th mat-header-cell *matHeaderCellDef>Monto</th><td mat-cell *matCellDef="let p">{{p.amount | currency:'USD'}}</td></ng-container>
              <ng-container matColumnDef="status"><th mat-header-cell *matHeaderCellDef>Estado</th><td mat-cell *matCellDef="let p">{{p.status}}</td></ng-container>
              <ng-container matColumnDef="card"><th mat-header-cell *matHeaderCellDef>Tarjeta</th><td mat-cell *matCellDef="let p">**** {{p.cardLast4}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="paymentCols"></tr><tr mat-row *matRowDef="let row; columns: paymentCols;"></tr>
            </table>
          </section>
        </mat-tab>
        <mat-tab label="Aeropuertos">
          <section class="surface grid">
            <form class="form-row" (ngSubmit)="saveAirport()">
              <mat-form-field><mat-label>Nombre</mat-label><input matInput name="an" [(ngModel)]="airport.name"></mat-form-field>
              <mat-form-field><mat-label>Ciudad</mat-label><input matInput name="ac" [(ngModel)]="airport.city"></mat-form-field>
              <mat-form-field><mat-label>Pais</mat-label><input matInput name="ap" [(ngModel)]="airport.country"></mat-form-field>
              <mat-form-field><mat-label>IATA</mat-label><input matInput name="ai" [(ngModel)]="airport.iataCode"></mat-form-field>
              <button mat-flat-button class="sky-button">Guardar</button>
            </form>
            <table mat-table [dataSource]="airports()">
              <ng-container matColumnDef="iata"><th mat-header-cell *matHeaderCellDef>IATA</th><td mat-cell *matCellDef="let a">{{a.iataCode}}</td></ng-container>
              <ng-container matColumnDef="name"><th mat-header-cell *matHeaderCellDef>Nombre</th><td mat-cell *matCellDef="let a">{{a.name}}</td></ng-container>
              <ng-container matColumnDef="city"><th mat-header-cell *matHeaderCellDef>Ciudad</th><td mat-cell *matCellDef="let a">{{a.city}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="airportCols"></tr><tr mat-row *matRowDef="let row; columns: airportCols;"></tr>
            </table>
          </section>
        </mat-tab>
        <mat-tab label="Aviones">
          <section class="surface grid">
            <form class="form-row" (ngSubmit)="saveAircraft()">
              <mat-form-field><mat-label>Modelo</mat-label><input matInput name="pm" [(ngModel)]="plane.model"></mat-form-field>
              <mat-form-field><mat-label>Capacidad</mat-label><input matInput type="number" name="pc" [(ngModel)]="plane.capacity"></mat-form-field>
              <mat-form-field><mat-label>Aerolinea</mat-label><input matInput name="pa" [(ngModel)]="plane.airline"></mat-form-field>
              <button mat-flat-button class="sky-button">Guardar</button>
            </form>
            <table mat-table [dataSource]="aircraft()">
              <ng-container matColumnDef="model"><th mat-header-cell *matHeaderCellDef>Modelo</th><td mat-cell *matCellDef="let p">{{p.model}}</td></ng-container>
              <ng-container matColumnDef="capacity"><th mat-header-cell *matHeaderCellDef>Capacidad</th><td mat-cell *matCellDef="let p">{{p.capacity}}</td></ng-container>
              <ng-container matColumnDef="airline"><th mat-header-cell *matHeaderCellDef>Aerolinea</th><td mat-cell *matCellDef="let p">{{p.airline}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="planeCols"></tr><tr mat-row *matRowDef="let row; columns: planeCols;"></tr>
            </table>
          </section>
        </mat-tab>
        <mat-tab label="Vuelos">
          <section class="surface grid">
            <form class="form-row" (ngSubmit)="saveFlight()">
              <mat-form-field><mat-label>Origen ID</mat-label><input matInput type="number" name="fo" [(ngModel)]="flight.originId"></mat-form-field>
              <mat-form-field><mat-label>Destino ID</mat-label><input matInput type="number" name="fd" [(ngModel)]="flight.destinationId"></mat-form-field>
              <mat-form-field><mat-label>Avion ID</mat-label><input matInput type="number" name="fa" [(ngModel)]="flight.aircraftId"></mat-form-field>
              <mat-form-field><mat-label>Salida ISO</mat-label><input matInput name="fs" [(ngModel)]="flight.departureTime"></mat-form-field>
              <mat-form-field><mat-label>Llegada ISO</mat-label><input matInput name="fl" [(ngModel)]="flight.arrivalTime"></mat-form-field>
              <mat-form-field><mat-label>Precio</mat-label><input matInput type="number" name="fp" [(ngModel)]="flight.price"></mat-form-field>
              <button mat-flat-button class="sky-button">Crear vuelo</button>
            </form>
            <table mat-table [dataSource]="flights()">
              <ng-container matColumnDef="route"><th mat-header-cell *matHeaderCellDef>Ruta</th><td mat-cell *matCellDef="let f">{{f.origin.iataCode}} - {{f.destination.iataCode}}</td></ng-container>
              <ng-container matColumnDef="time"><th mat-header-cell *matHeaderCellDef>Salida</th><td mat-cell *matCellDef="let f">{{f.departureTime | date:'short'}}</td></ng-container>
              <ng-container matColumnDef="price"><th mat-header-cell *matHeaderCellDef>Precio</th><td mat-cell *matCellDef="let f">{{f.price | currency:'USD'}}</td></ng-container>
              <tr mat-header-row *matHeaderRowDef="flightCols"></tr><tr mat-row *matRowDef="let row; columns: flightCols;"></tr>
            </table>
          </section>
        </mat-tab>
      </mat-tab-group>
    </main>
  `,
  styles: [`.metric h2{font-size:30px;margin:8px 0 0} small{color:#667085}`]
})
export class AdminDashboardComponent implements OnInit {
  dashboard = signal<Dashboard | null>(null); airports = signal<Airport[]>([]); aircraft = signal<Aircraft[]>([]); flights = signal<Flight[]>([]);
  customers = signal<Customer[]>([]); reservations = signal<Reservation[]>([]); payments = signal<Payment[]>([]);
  airportCols = ['iata', 'name', 'city']; planeCols = ['model', 'capacity', 'airline']; flightCols = ['route', 'time', 'price'];
  customerCols = ['name', 'phone', 'doc', 'reservations']; reservationCols = ['code', 'route', 'seat', 'status', 'email'];
  paymentCols = ['reservation', 'amount', 'status', 'card'];
  airport: Partial<Airport> = {}; plane: Partial<Aircraft> = {};
  flight: any = { status: 'SCHEDULED', departureTime: new Date(Date.now() + 86400000).toISOString(), arrivalTime: new Date(Date.now() + 93600000).toISOString() };
  constructor(private api: AirportApiService) {}
  ngOnInit() { this.reload(); }
  reload() {
    this.api.dashboard().subscribe(v => this.dashboard.set(v));
    this.api.adminAirports().subscribe(v => this.airports.set(v));
    this.api.adminAircraft().subscribe(v => this.aircraft.set(v));
    this.api.adminFlights().subscribe(v => this.flights.set(v));
    this.api.customers().subscribe(v => this.customers.set(v));
    this.api.crmReservations().subscribe(v => this.reservations.set(v));
    this.api.payments().subscribe(v => this.payments.set(v));
  }
  saveAirport() { this.api.saveAirport(this.airport).subscribe(() => { this.airport = {}; this.reload(); }); }
  saveAircraft() { this.api.saveAircraft(this.plane).subscribe(() => { this.plane = {}; this.reload(); }); }
  saveFlight() { this.api.saveFlight(this.flight).subscribe(() => this.reload()); }
}
