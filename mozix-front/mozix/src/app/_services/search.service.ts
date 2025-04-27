// search.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface Movie { // Hozzáadjuk a Movie interfacet a filmek típusához
    cover: string;
    actors: { image: string; name: string; id: number }[];
    genres: { name: string; id: number }[];
    averageRating: number;
    directors: { image: string; name: string; id: number }[];
    length: number;
    description: string;
    movieId: number;
    title: string;
    releaseYear: number;
    trailerLink: string;
}

interface SearchResponse {
    movies: Movie[]; // Módosítjuk a results-ot movies-ra, és a típusát Movie[]-re
    searchTerm: string;
    count: number;
    status: string;
}

@Injectable({
    providedIn: 'root'
})
export class SearchService {
    private apiUrl = 'http://localhost:8080/mozixx/resources/movies/search';

    constructor(private http: HttpClient) { }

    search(query: string): Observable<SearchResponse> {
        const params = new HttpParams().set('q', query);
        return this.http.get<SearchResponse>(this.apiUrl, { params });
    }
}