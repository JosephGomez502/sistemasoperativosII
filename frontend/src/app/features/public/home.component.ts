import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule],
  template: `
    <section class="hero">
      <div class="page">
        <h1>AeroPort</h1>
        <p>Compra vuelos, administra reservas y descarga tickets con QR desde una experiencia aeroportuaria segura y lista para operación.</p>
        <a mat-flat-button color="accent" routerLink="/flights"><mat-icon>search</mat-icon>Buscar vuelos</a>
      </div>
    </section>
    <section class="page cards">
      <article class="surface"><h2>Boletos digitales</h2><p>PDF compacto compatible con impresora térmica y validación QR.</p></article>
      <article class="surface"><h2>Paneles seguros</h2><p>Roles ADMIN y CLIENT con protección JWT y rutas separadas.</p></article>
      <article class="surface"><h2>Operación aérea</h2><p>CRUD de aeropuertos, aviones, vuelos y asientos.</p></article>
    </section>
  `
})
export class HomeComponent {}
