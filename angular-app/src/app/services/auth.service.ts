import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

interface AuthResponse {
  token: string;
  username: string;
  authorities?: { authority: string }[];
}

interface User {
  username: string;
  roles?: string[];
  authorities?: { authority: string }[];
}

interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

interface LoginRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenKey = 'auth_token';
  private userKey = 'auth_user';

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.loadFromStorage();
  }

  private loadFromStorage(): void {
    const token = localStorage.getItem(this.tokenKey);
    const userJson = localStorage.getItem(this.userKey);

    if (token && userJson) {
      const user = JSON.parse(userJson);
      this.currentUserSubject.next(user);
      this.isAuthenticatedSubject.next(true);
    }
  }

  register(registerData: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/register`, registerData)
      .pipe(tap((response) => this.handleAuthentication(response)));
  }
  login(loginData: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, loginData)
      .pipe(tap((response) => this.handleAuthentication(response)));
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
  private handleAuthentication(response: AuthResponse): void {
    const { token, username, authorities } = response;

    // Extract role names from authorities array
    const roles = authorities?.map((auth) => auth.authority) || [];

    const user: User = { username, roles, authorities };

    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.userKey, JSON.stringify(user));

    this.currentUserSubject.next(user);
    this.isAuthenticatedSubject.next(true);
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.value;
  }
  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    if (!user || !user.roles) {
      return false;
    }

    return user.roles.includes('ROLE_ADMIN');
  }
}
