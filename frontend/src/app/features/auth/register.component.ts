import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { AuthService } from '../../core/services/auth.service';

@Component({
  standalone: true,
  imports: [FormsModule, RouterLink, MatButtonModule, MatInputModule],
  template: `
    <main class="page">
      <form class="surface grid auth" (ngSubmit)="register()">
        <h1>Registro</h1>
        <mat-form-field><mat-label>Nombre completo</mat-label><input matInput name="fullName" [(ngModel)]="fullName" required></mat-form-field>
        <mat-form-field><mat-label>Email</mat-label><input matInput name="email" type="email" [(ngModel)]="email" required></mat-form-field>
        <mat-form-field><mat-label>Documento</mat-label><input matInput name="documentId" [(ngModel)]="documentId"></mat-form-field>
        <mat-form-field><mat-label>Teléfono</mat-label><input matInput name="phone" [(ngModel)]="phone"></mat-form-field>
        <mat-form-field><mat-label>Contraseña</mat-label><input matInput name="password" type="password" [(ngModel)]="password" required></mat-form-field>
        @if (error) { <p class="error">{{error}}</p> }
        <button mat-flat-button color="primary">Crear cuenta</button>
        <a routerLink="/login">Ya tengo cuenta</a>
      </form>
    </main>
  `,
  styles: [`.auth{max-width:520px;margin:30px auto}.error{color:#b3261e}`]
})
export class RegisterComponent {
  fullName = ''; email = ''; documentId = ''; phone = ''; password = ''; error = '';
  constructor(private auth: AuthService, private router: Router) {}
  register() {
    this.auth.register({ fullName: this.fullName, email: this.email, password: this.password, phone: this.phone, documentId: this.documentId })
      .subscribe({ next: () => this.router.navigateByUrl('/client'), error: e => this.error = e.error?.message ?? 'No se pudo registrar' });
  }
}
