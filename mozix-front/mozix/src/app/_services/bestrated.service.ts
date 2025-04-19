// category.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; // Add this import

@Injectable({
  providedIn: 'root'
})
export class BestratedService {
  private apiUrl = 'http://localhost:8080/mozixx/resources/ratings/by-rounded-rating/5';
  private apiUrl2 = 'http://localhost:8080/mozixx/resources/ratings/by-rounded-rating/4';
  private apiUrl3 = 'http://localhost:8080/mozixx/resources/ratings/by-rounded-rating/3';
  private apiUrl4 = 'http://localhost:8080/mozixx/resources/ratings/by-rounded-rating/2';
  private apiUrl5 = 'http://localhost:8080/mozixx/resources/ratings/by-rounded-rating/1';

  constructor(private http: HttpClient) { }

  getMovies(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.movies) // Adjust 'movies' based on the actual API response structure
    );
  }


  getMovies2(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl2).pipe(
      map(response => response.movies) // Adjust 'movies' based on the actual API response structure
    );
  }

  getMovies3(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl3).pipe(
      map(response => response.movies) // Adjust 'movies' based on the actual API response structure
    );
  }

  getMovies4(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl4).pipe(
      map(response => response.movies) // Adjust 'movies' based on the actual API response structure
    );
  }

  getMovies5(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl5).pipe(
      map(response => response.movies) // Adjust 'movies' based on the actual API response structure
    );
  }
}