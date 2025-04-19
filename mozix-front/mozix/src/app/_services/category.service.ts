// category.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators'; // Add this import

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = 'http://localhost:8080/mozixx/resources/genres/3/movies';
  private apiUrl2 = 'http://localhost:8080/mozixx/resources/genres/1/movies';
  private apiUrl3 = 'http://localhost:8080/mozixx/resources/genres/6/movies';
  private apiUrl4 = 'http://localhost:8080/mozixx/resources/genres/19/movies';

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

  getComedyMovies(): Observable<any[]> {
    // Verify the API endpoint is correct
    return this.http.get<any>('http://localhost:8080/mozixx/resources/genres/3/movies')
      .pipe(
        map(response => {
          console.log('API Response:', response); // Add this for debugging
          return response.movies || [];
        }),
        )
      ;
  }

  getDramaMovies(): Observable<any[]> {
    // Verify the API endpoint is correct
    return this.http.get<any>('http://localhost:8080/mozixx/resources/genres/3/movies')
      .pipe(
        map(response => {
          console.log('API Response:', response); // Add this for debugging
          return response.movies || [];
        }),
        )
      ;
  }

  getHistoricalMovies(): Observable<any[]> {
    // Verify the API endpoint is correct
    return this.http.get<any>('http://localhost:8080/mozixx/resources/genres/3/movies')
      .pipe(
        map(response => {
          console.log('API Response:', response); // Add this for debugging
          return response.movies || [];
        }),
        )
      ;
  }

  
}