// category.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private baseUrl = 'http://localhost:8080/mozixx/resources/genres';

  constructor(private http: HttpClient) { }

  getMovies(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/3/movies-with-details`).pipe(
      map(response => response.movies)
    );
  }

  getMovies2(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/1/movies-with-details`).pipe(
      map(response => response.movies)
    );
  }

  getMovies3(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/19/movies-with-details`).pipe(
      map(response => response.movies)
    );
  }

  getVígjátékMovies(): Observable<any[]> {
    console.log('CategoryService.getvígjátékMovies() meghívva');
    return this.http.get<any>(`${this.baseUrl}/3/movies-with-details`).pipe(
      map(response => {
        console.log('CategoryService.getvígjátékMovies() válasz:', response);
        return response.movies || [];
      })
    );
  }

  getDrámaMovies(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/1/movies-with-details`).pipe(
      map(response => response.movies || [])
    );
  }

  getHáborúsMovies(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/6/movies-with-details`).pipe(
      map(response => response.movies || [])
    );
  }

  getWarMovies(): Observable<any[]> {
    return this.http.get<any>(`${this.baseUrl}/19/movies-with-details`).pipe(
      map(response => response.movies || [])
    );
  }

  

}
