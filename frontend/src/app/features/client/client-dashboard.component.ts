import { Component, OnInit, signal } from '@angular/core';
import { DatePipe, CurrencyPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { AirportApiService } from '../../core/services/airport-api.service';
import { Reservation } from '../../core/services/api.types';

@Component({
  standalone: true,
  imports: [DatePipe, CurrencyPipe, MatButtonModule, MatTableModule],
  template: `
    <main class="page grid">
      <h1>Panel cliente</h1>
      <section class="surface">
        <table mat-table [dataSource]="reservations()">
          <ng-container matColumnDef="code"><th mat-header-cell *matHeaderCellDef>Reserva</th><td mat-cell *matCellDef="let r">{{r.code}}</td></ng-container>
          <ng-container matColumnDef="route"><th mat-header-cell *matHeaderCellDef>Ruta</th><td mat-cell *matCellDef="let r">{{r.flight.origin.iataCode}} - {{r.flight.destination.iataCode}}</td></ng-container>
          <ng-container matColumnDef="seat"><th mat-header-cell *matHeaderCellDef>Asiento</th><td mat-cell *matCellDef="let r">{{r.seat.seatNumber}}</td></ng-container>
          <ng-container matColumnDef="price"><th mat-header-cell *matHeaderCellDef>Precio</th><td mat-cell *matCellDef="let r">{{r.flight.price | currency:'USD'}}</td></ng-container>
          <ng-container matColumnDef="ticket"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let r"><a mat-stroked-button [href]="api.ticketUrl(r.code)" target="_blank">PDF</a></td></ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr><tr mat-row *matRowDef="let row; columns: cols;"></tr>
        </table>
      </section>
    </main>
  `
})
export class ClientDashboardComponent implements OnInit {
  cols = ['code', 'route', 'seat', 'price', 'ticket']; reservations = signal<Reservation[]>([]);
  constructor(public api: AirportApiService) {}
  ngOnInit() { this.api.reservations().subscribe(v => this.reservations.set(v)); }
}
