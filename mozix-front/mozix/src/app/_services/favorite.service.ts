// _services/favorite.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
 providedIn: 'root'
})
export class FavoriteService {
 private addFavoriteUrl = 'http://localhost:8080/mozixx/resources/favorites/add';
 private listFavoritesUrl = 'http://localhost:8080/mozixx/resources/favorites/all';
 private removeFavoriteUrl = 'http://localhost:8080/mozixx/resources/favorites/remove';
 private getMovieUrl = 'http://localhost:8080/mozixx/resources/movies'; // Ezt a sort add hozzá

 constructor(private http: HttpClient) { }

 private getHeaders(): HttpHeaders {
   const token = localStorage.getItem('jwtToken');
   return new HttpHeaders({
     'Authorization': `Bearer ${token}`
   });
 }

 addFavorite(movieId: number): Observable<any> {
   return this.http.post(this.addFavoriteUrl, { movieId }, { headers: this.getHeaders() });
 }

 listFavorites(): Observable<any> {
   return this.http.get(this.listFavoritesUrl, { headers: this.getHeaders() });
 }

 removeFavorite(movieId: number): Observable<any> {
   return this.http.delete(`${this.removeFavoriteUrl}/${movieId}`, { headers: this.getHeaders() });
 }

 // Új funkció a film lekéréséhez az ID alapján
 getMovie(movieId: number): Observable<any> { // Ezt a funkciót add hozzá
   return this.http.get(`${this.getMovieUrl}/${movieId}`, { headers: this.getHeaders() });
 }
}