import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse } from './api.types';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = environment.apiUrl;
  token = signal(localStorage.getItem('accessToken'));
  role = signal(localStorage.getItem('role') as 'ADMIN' | 'CLIENT' | null);
  fullName = signal(localStorage.getItem('fullName'));
  constructor(private http: HttpClient, private router: Router) {}
  isLoggedIn() { return !!this.token(); }
  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${this.api}/auth/login`, { email, password }).pipe(tap(r => this.store(r)));
  }
  register(payload: { fullName: string; email: string; password: string; phone?: string; documentId?: string }) {
    return this.http.post<AuthResponse>(`${this.api}/auth/register`, payload).pipe(tap(r => this.store(r)));
  }
  logout() {
    localStorage.clear(); this.token.set(null); this.role.set(null); this.fullName.set(null); this.router.navigateByUrl('/');
  }
  private store(r: AuthResponse) {
    localStorage.setItem('accessToken', r.accessToken);
    localStorage.setItem('refreshToken', r.refreshToken);
    localStorage.setItem('role', r.role);
    localStorage.setItem('fullName', r.fullName);
    this.token.set(r.accessToken); this.role.set(r.role); this.fullName.set(r.fullName);
  }
}
