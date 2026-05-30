import { Component, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { AirportApiService } from '../../core/services/airport-api.service';
import { Flight } from '../../core/services/api.types';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink, CurrencyPipe, DatePipe, MatButtonModule, MatInputModule, MatTableModule],
  template: `
    <main class="page grid">
      <div class="stepper">
        <span class="active"><strong>1</strong>Búsqueda</span><span class="active"><strong>2</strong>Resultados</span><span><strong>3</strong>Cliente</span><span><strong>4</strong>Pasajero</span><span><strong>5</strong>Servicios</span><span><strong>6</strong>Reservar</span>
      </div>
      <h1>Buscar vuelos</h1>
      <form class="surface form-row" (ngSubmit)="load()">
        <mat-form-field><mat-label>Origen IATA</mat-label><input matInput name="origin" [(ngModel)]="origin"></mat-form-field>
        <mat-form-field><mat-label>Destino IATA</mat-label><input matInput name="destination" [(ngModel)]="destination"></mat-form-field>
        <button mat-flat-button color="primary">Buscar</button>
      </form>
      <div class="surface">
        <table mat-table [dataSource]="flights()">
          <ng-container matColumnDef="route"><th mat-header-cell *matHeaderCellDef>Ruta</th><td mat-cell *matCellDef="let f">{{f.origin.iataCode}} - {{f.destination.iataCode}}</td></ng-container>
          <ng-container matColumnDef="time"><th mat-header-cell *matHeaderCellDef>Salida</th><td mat-cell *matCellDef="let f">{{f.departureTime | date:'medium'}}</td></ng-container>
          <ng-container matColumnDef="price"><th mat-header-cell *matHeaderCellDef>Precio</th><td mat-cell *matCellDef="let f">{{f.price | currency:'USD'}}</td></ng-container>
          <ng-container matColumnDef="seats"><th mat-header-cell *matHeaderCellDef>Asientos</th><td mat-cell *matCellDef="let f">{{f.availableSeats}}</td></ng-container>
          <ng-container matColumnDef="action"><th mat-header-cell *matHeaderCellDef></th><td mat-cell *matCellDef="let f"><a mat-stroked-button [routerLink]="['/checkout', f.id]">Comprar</a></td></ng-container>
          <tr mat-header-row *matHeaderRowDef="cols"></tr><tr mat-row *matRowDef="let row; columns: cols;"></tr>
        </table>
      </div>
    </main>
  `
})
export class SearchFlightsComponent implements OnInit {
  origin = ''; destination = ''; cols = ['route', 'time', 'price', 'seats', 'action']; flights = signal<Flight[]>([]);
  constructor(private api: AirportApiService) {}
  ngOnInit() { this.load(); }
  load() { this.api.flights(this.origin, this.destination).subscribe(v => this.flights.set(v)); }
}
