import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MainpageService {
  private apiUrl = 'http://localhost:8080/mozixx/resources/movies/all';
  private apiUrl2 = 'http://localhost:8080/mozixx/resources/movies/random';
  private apiUrl3 = 'http://localhost:8080/mozixx/resources/movies';

  constructor(private http: HttpClient) { }

  getMovies(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.movies) // Extract the movies array
    );
  }

  getRandomMovies(): Observable<any[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.movies) // Extract the movies array
    );
  }

  getMovieById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }
}