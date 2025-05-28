import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private baseUrl = 'http://localhost:8080/mozixx/resources/movies';

  constructor(private http: HttpClient) { }

  deleteMovie(movieId: number, token: string): Observable<any> {
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}` // Feltételezve, hogy a JWT token egy Bearer token
    });
    return this.http.delete(`${this.baseUrl}/${movieId}`, { headers });
  }
}