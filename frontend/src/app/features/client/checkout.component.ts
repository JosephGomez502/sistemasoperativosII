import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { AirportApiService } from '../../core/services/airport-api.service';
import { Seat } from '../../core/services/api.types';

@Component({
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatInputModule, MatSelectModule],
  template: `
    <main class="page">
      <form class="surface grid checkout" (ngSubmit)="pay()">
        <div class="stepper">
          <span class="active"><strong>1</strong>Busqueda</span><span class="active"><strong>2</strong>Resultados</span><span class="active"><strong>3</strong>Cliente</span><span class="active"><strong>4</strong>Pasajero</span><span><strong>5</strong>Servicios</span><span><strong>6</strong>Reservar</span>
        </div>
        <h1>Registro de pasajero y pago</h1>
        <section class="passenger">
          <h2>Pasajero 1 - Adulto</h2>
          <div class="form-row">
            <mat-form-field><mat-label>Titulo</mat-label><mat-select name="title" [(ngModel)]="title" required><mat-option value="Sr.">Sr.</mat-option><mat-option value="Sra.">Sra.</mat-option></mat-select></mat-form-field>
            <mat-form-field><mat-label>Genero</mat-label><mat-select name="gender" [(ngModel)]="gender" required><mat-option value="MASCULINO">Masculino</mat-option><mat-option value="FEMENINO">Femenino</mat-option></mat-select></mat-form-field>
            <mat-form-field><mat-label>Fecha nacimiento</mat-label><input matInput name="birth" [(ngModel)]="birthDate" placeholder="1995-05-30" required></mat-form-field>
            <mat-form-field><mat-label>Nacionalidad</mat-label><input matInput name="nat" [(ngModel)]="nationality" required></mat-form-field>
          </div>
          <div class="form-row">
            <mat-form-field><mat-label>Tipo documento</mat-label><mat-select name="docType" [(ngModel)]="documentType" required><mat-option value="DPI">DPI</mat-option><mat-option value="PASAPORTE">Pasaporte</mat-option></mat-select></mat-form-field>
            <mat-form-field><mat-label>Numero documento</mat-label><input matInput name="doc" [(ngModel)]="documentId" required></mat-form-field>
            <mat-form-field><mat-label>Expiracion</mat-label><input matInput name="docExp" [(ngModel)]="documentExpiration" placeholder="2030-12-31" required></mat-form-field>
            <mat-form-field><mat-label>Pais emision</mat-label><input matInput name="docCountry" [(ngModel)]="documentCountry" required></mat-form-field>
          </div>
        </section>
        <mat-form-field><mat-label>Asiento</mat-label><mat-select name="seat" [(ngModel)]="seatNumber" required>
          @for (s of seats(); track s.id) { <mat-option [value]="s.seatNumber" [disabled]="s.reserved">{{s.seatNumber}}</mat-option> }
        </mat-select></mat-form-field>
        <mat-form-field><mat-label>Titular</mat-label><input matInput name="holder" [(ngModel)]="cardHolder" required></mat-form-field>
        <mat-form-field><mat-label>Tarjeta</mat-label><input matInput name="card" [(ngModel)]="cardNumber" required></mat-form-field>
        <div class="form-row">
          <mat-form-field><mat-label>MM/AA</mat-label><input matInput name="expiry" [(ngModel)]="expiry" required></mat-form-field>
          <mat-form-field><mat-label>CVV</mat-label><input matInput name="cvv" [(ngModel)]="cvv" required></mat-form-field>
        </div>
        @if (error) { <p class="error">{{error}}</p> }
        <button mat-flat-button class="sky-button">Pagar, emitir ticket y enviar correo</button>
      </form>
    </main>
  `,
  styles: [`.checkout{max-width:980px;margin:30px auto}.error{color:#b3261e}`]
})
export class CheckoutComponent implements OnInit {
  flightId = 0;
  seats = signal<Seat[]>([]);
  seatNumber = '';
  cardHolder = '';
  cardNumber = '4111111111111111';
  expiry = '12/30';
  cvv = '123';
  error = '';
  title = 'Sr.';
  gender = 'MASCULINO';
  birthDate = '';
  nationality = 'Guatemala';
  documentType = 'DPI';
  documentId = '';
  documentExpiration = '';
  documentCountry = 'Guatemala';
  frequentFlyer = '';

  constructor(private route: ActivatedRoute, private api: AirportApiService, private router: Router) {}

  ngOnInit() {
    this.flightId = Number(this.route.snapshot.paramMap.get('flightId'));
    this.api.seats(this.flightId).subscribe(v => this.seats.set(v));
  }

  pay() {
    this.api.checkout({
      flightId: this.flightId,
      seatNumber: this.seatNumber,
      cardHolder: this.cardHolder,
      cardNumber: this.cardNumber,
      expiry: this.expiry,
      cvv: this.cvv,
      title: this.title,
      gender: this.gender,
      birthDate: this.birthDate,
      nationality: this.nationality,
      documentType: this.documentType,
      documentId: this.documentId,
      documentExpiration: this.documentExpiration,
      documentCountry: this.documentCountry,
      frequentFlyer: this.frequentFlyer
    }).subscribe({
      next: () => this.router.navigateByUrl('/client'),
      error: e => this.error = e.error?.message ?? 'Pago rechazado'
    });
  }
}
