import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule, MatIconModule],
  template: `
    <mat-toolbar class="tag-bar">
      <a routerLink="/" class="brand"><span>TagAirlines</span><mat-icon>flight_takeoff</mat-icon></a>
      <span class="toolbar-spacer"></span>
      <a mat-button routerLink="/flights">Vuelos</a>
      @if (isAdmin()) { <a mat-button routerLink="/admin">Admin</a> }
      @if (isLogged()) { <a mat-button routerLink="/client">Mis boletos</a><button mat-icon-button (click)="logout()" aria-label="Salir"><mat-icon>logout</mat-icon></button> }
      @else { <a mat-button routerLink="/login">Ingresar</a> }
    </mat-toolbar>
    <router-outlet />
  `,
  styles: [`.brand{display:flex;gap:8px;align-items:center;font-weight:800;font-size:28px}.brand span{letter-spacing:0} mat-toolbar{height:72px}`]
})
export class AppComponent {
  private auth = inject(AuthService);
  isLogged = computed(() => this.auth.isLoggedIn());
  isAdmin = computed(() => this.auth.role() === 'ADMIN');
  logout() { this.auth.logout(); }
}
