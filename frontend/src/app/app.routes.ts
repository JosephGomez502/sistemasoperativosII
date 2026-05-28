import { Routes } from '@angular/router';
import { HomeComponent } from './features/public/home.component';
import { SearchFlightsComponent } from './features/public/search-flights.component';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';
import { ClientDashboardComponent } from './features/client/client-dashboard.component';
import { CheckoutComponent } from './features/client/checkout.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard.component';
import { authGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'flights', component: SearchFlightsComponent },
  { path: 'checkout/:flightId', component: CheckoutComponent, canActivate: [authGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'client', component: ClientDashboardComponent, canActivate: [authGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [adminGuard] },
  { path: '**', redirectTo: '' }
];
