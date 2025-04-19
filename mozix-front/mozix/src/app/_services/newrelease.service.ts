import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NewreleaseService {
  private apiUrl = 'http://localhost:8080/mozixx/resources/movies/latest';

  constructor(private http: HttpClient) { }

  getLatestMovies(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  } 
  
}

