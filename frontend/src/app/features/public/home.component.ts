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
        <h1>recuerdos para toda la vida</h1>
        <p>Vuela desde Guatemala hacia destinos premium con compra segura, ticket digital compacto y confirmacion por correo.</p>
        <a mat-flat-button class="sky-button" routerLink="/flights"><mat-icon>search</mat-icon>Buscar vuelos</a>
      </div>
    </section>
    <section class="booking-strip">
      <div class="page strip-grid">
        <div><small>Origen</small><strong>Guatemala (GUA)</strong></div>
        <div><small>Destino</small><strong>Belize City (BZE)</strong></div>
        <div><small>Pasajeros</small><strong>1 adulto</strong></div>
        <a mat-flat-button class="sky-button" routerLink="/flights">Buscar</a>
      </div>
    </section>
    <section class="page">
      <h1>Destinos populares</h1>
      <div class="cards">
        <article class="surface destination"><img src="https://images.unsplash.com/photo-1587330979470-3595ac045ab0?auto=format&fit=crop&w=900&q=80" alt="Flores"><h2>Flores</h2><p>Guatemala</p></article>
        <article class="surface destination"><img src="https://images.unsplash.com/photo-1510414842594-a61c69b5ae57?auto=format&fit=crop&w=900&q=80" alt="Belice"><h2>Belice</h2><p>Ida y vuelta desde <strong>$446 USD</strong></p></article>
        <article class="surface destination"><img src="https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=900&q=80" alt="Roatan"><h2>Roatán</h2><p>Ida y vuelta desde <strong>$399 USD</strong></p></article>
      </div>
    </section>
    <section class="page cards">
      <article class="surface"><h2>Boletos digitales</h2><p>PDF compacto compatible con impresora térmica y validación QR.</p></article>
      <article class="surface"><h2>Paneles seguros</h2><p>Roles ADMIN y CLIENT con protección JWT y rutas separadas.</p></article>
      <article class="surface"><h2>Operación aérea</h2><p>CRUD de aeropuertos, aviones, vuelos y asientos.</p></article>
    </section>
  `,
  styles: [`
    .booking-strip{background:#111f4c;margin-top:-1px}
    .strip-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;align-items:center}
    .strip-grid div{background:white;padding:16px 20px}.strip-grid small{display:block;color:#28b7e3}.strip-grid strong{font-size:20px}
    .destination{padding:0;text-align:center;overflow:hidden}.destination img{width:100%;height:210px;object-fit:cover}.destination h2{font-size:32px;margin:26px 0 4px}.destination p{font-size:18px}
    @media(max-width:760px){.strip-grid{grid-template-columns:1fr}.destination img{height:170px}}
  `]
})
export class HomeComponent {}
