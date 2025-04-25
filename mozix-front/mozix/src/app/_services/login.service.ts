// login.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  private loginUrl = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/users/login';
  private registerUrl = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/users/register';

  constructor(private http: HttpClient) { }

  login(credentials: any): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, credentials);
  }

  register(userData: any): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(this.registerUrl, userData);
  }
}