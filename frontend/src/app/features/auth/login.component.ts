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
      <form class="surface grid auth" (ngSubmit)="login()">
        <h1>Ingresar</h1>
        <mat-form-field><mat-label>Email</mat-label><input matInput type="email" name="email" [(ngModel)]="email" required></mat-form-field>
        <mat-form-field><mat-label>Contraseña</mat-label><input matInput type="password" name="password" [(ngModel)]="password" required></mat-form-field>
        @if (error) { <p class="error">{{error}}</p> }
        <button mat-flat-button color="primary">Entrar</button>
        <a routerLink="/register">Crear cuenta</a>
      </form>
    </main>
  `,
  styles: [`.auth{max-width:420px;margin:40px auto}.error{color:#b3261e}`]
})
export class LoginComponent {
  email = 'admin@airport.local'; password = 'Admin12345'; error = '';
  constructor(private auth: AuthService, private router: Router) {}
  login() {
    this.auth.login(this.email, this.password).subscribe({
      next: r => this.router.navigateByUrl(r.role === 'ADMIN' ? '/admin' : '/client'),
      error: e => this.error = e.error?.message ?? 'No se pudo iniciar sesion'
    });
  }
}
