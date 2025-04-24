import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

interface Genre { // Ha a Movie interfészedben van Genre
  name: string;
  id: string;
}

interface Movie {
  cover?: string;
  title?: string;
  roundedRating?: number;
  genres?: Genre[]; // Feltételezve, hogy van ilyen
  // ... egyéb tulajdonságok
}

@Injectable({
  providedIn: 'root'
})
export class BestratedService {
  private apiUrl = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/ratings/by-rounded-rating/5';
  private apiUrl2 = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/ratings/by-rounded-rating/4';
  private apiUrl3 = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/ratings/by-rounded-rating/3';
  private apiUrl4 = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/ratings/by-rounded-rating/2';
  private apiUrl5 = 'http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/ratings/by-rounded-rating/1';

  constructor(private http: HttpClient) { }

  getMovies(): Observable<Movie[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.movies as Movie[])
    );
  }

  getMovies2(): Observable<Movie[]> {
    return this.http.get<any>(this.apiUrl2).pipe(
      map(response => response.movies as Movie[])
    );
  }

  getMovies3(): Observable<Movie[]> {
    return this.http.get<any>(this.apiUrl3).pipe(
      map(response => response.movies as Movie[])
    );
  }

  getMovies4(): Observable<Movie[]> {
    return this.http.get<any>(this.apiUrl4).pipe(
      map(response => response.movies as Movie[])
    );
  }

  getMovies5(): Observable<Movie[]> {
    return this.http.get<any>(this.apiUrl5).pipe(
      map(response => response.movies as Movie[])
    );
  }

  getMovieById(id: number): Observable<any> {
    return this.http.get<any>(`http://localhost:8080/mozixx-1.0-SNAPSHOT/resources/movies/${id}`);
  }
}