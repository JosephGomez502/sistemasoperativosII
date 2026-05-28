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
        <h1>Checkout seguro</h1>
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
        <button mat-flat-button color="primary">Pagar y emitir ticket</button>
      </form>
    </main>
  `,
  styles: [`.checkout{max-width:560px;margin:30px auto}.error{color:#b3261e}`]
})
export class CheckoutComponent implements OnInit {
  flightId = Number(this.route.snapshot.paramMap.get('flightId')); seats = signal<Seat[]>([]);
  seatNumber = ''; cardHolder = ''; cardNumber = '4111111111111111'; expiry = '12/30'; cvv = '123'; error = '';
  constructor(private route: ActivatedRoute, private api: AirportApiService, private router: Router) {}
  ngOnInit() { this.api.seats(this.flightId).subscribe(v => this.seats.set(v)); }
  pay() {
    this.api.checkout({ flightId: this.flightId, seatNumber: this.seatNumber, cardHolder: this.cardHolder, cardNumber: this.cardNumber, expiry: this.expiry, cvv: this.cvv })
      .subscribe({ next: () => this.router.navigateByUrl('/client'), error: e => this.error = e.error?.message ?? 'Pago rechazado' });
  }
}
