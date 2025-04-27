// login.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http'; // Importáld a HttpHeaders-t
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Router } from '@angular/router';

export interface SuccessfulLoginResponse {
  result: {
    role: string;
    registrationDate: string;
    id: number;
    email: string;
    username: string;
  };
  jwt: string;
  status: 'success';
  statusCode: 200;
}

export interface FailedLoginResponse {
  message: string;
  status: 'error';
  statusCode: 401;
  jwt: null; // Vagy undefined, attól függően, hogy a backend mit küld
}

export type LoginResponse = SuccessfulLoginResponse | FailedLoginResponse;

interface SuccessfulRegisterResponse {
  message: string;
  status: 'success';
  statusCode: 201;
}

interface FailedRegisterResponse {
  message: string;
  status: 'error';
  statusCode: 409;
}

type RegisterResponse = SuccessfulRegisterResponse | FailedRegisterResponse;

interface SuccessfulDeleteResponse {
  message: string;
  status: 'success';
  statusCode: 200;
}

interface FailedDeleteResponse {
  message: string;
  status: 'error';
  statusCode: 400 | 401 | 404; // Például: hibás jelszó, jogosulatlan hozzáférés, nem található felhasználó
}

type DeleteResponse = SuccessfulDeleteResponse | FailedDeleteResponse;

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private loginUrl = 'http://localhost:8080/mozixx/resources/users/login';
  private registerUrl = 'http://localhost:8080/mozixx/resources/users/register';
  private deleteUserUrl = 'http://localhost:8080/mozixx/resources/users/delete'; // Itt ne add meg a konkrét ID-t
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, credentials).pipe(
      map(response => {
        if ((response as SuccessfulLoginResponse).jwt) {
          this.setToken((response as SuccessfulLoginResponse).jwt);
          localStorage.setItem('username', (response as SuccessfulLoginResponse).result.username);
          localStorage.setItem('id', (response as SuccessfulLoginResponse).result.id.toString());
          console.log('Bejelentkezéskor ID a localStorage-ban:', localStorage.getItem('id')); // Add hozzá ezt
          this.isLoggedInSubject.next(true);
        } else {
          this.isLoggedInSubject.next(false);
        }
        return response;
      })
    );
  }

  register(userData: any): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(this.registerUrl, userData);
  }

  logout(): void {
    this.removeToken();
    localStorage.removeItem('username');
    localStorage.removeItem('id');
    this.isLoggedInSubject.next(false);
    this.router.navigate(['/mainpage']); // Vagy a bejelentkezési oldalra, igény szerint
  }

  deleteUser(userId: string | null, password: string): Observable<DeleteResponse> {
    if (!userId) {
      console.error('Felhasználói azonosító nem található a törléshez.');
      return new Observable(observer => observer.error({ message: 'Felhasználói azonosító nem található.' }));
    }
    const url = `${this.deleteUserUrl}/${userId}`;
    const headers = new HttpHeaders({
      'x-password': password // Add meg az egyedi headert a jelszóval
    });
    return this.http.delete<DeleteResponse>(url, { headers }); // Küldd el a headereket a kéréssel
  }

  private setToken(token: string): void {
    localStorage.setItem('jwtToken', token);
  }

  private getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  private removeToken(): void {
    localStorage.removeItem('jwtToken');
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }
}