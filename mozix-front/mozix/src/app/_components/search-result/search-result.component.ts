import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { SearchService } from '../../_services/search.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FavoriteService } from '../../_services/favorite.service';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-search-results',
    standalone: true,
    imports: [
        NavbarComponent,
        RouterLink,
        RouterOutlet,
        CommonModule,
    ],
    templateUrl: './search-results.component.html',
    styleUrl: './search-results.component.css'
})
export class SearchResultsComponent implements OnInit {
    searchQuery: string = '';
    searchResults: any[] = [];
    selectedMovie: any = null;
    sanitizedTrailerLink: SafeResourceUrl | null = null;
    showModal = false;
    isSidebarOpen: boolean = false;
    selectedCategory: string = '';
    bookmarkedMovies: number[] = [];
    isBookmarked: boolean = false;
    similarMovies: any[] = [];
    loading: boolean = false;
    error: string = '';

    constructor(
        private searchService: SearchService,
        private sanitizer: DomSanitizer,
        private favoriteService: FavoriteService,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.searchQuery = params['q'];
            if (this.searchQuery) {
                this.performSearch(this.searchQuery);
            } else {
                this.searchResults = [];
            }
        });
        this.loadInitialBookmarks();
    }

    loadInitialBookmarks(): void {
        this.favoriteService.listFavorites().subscribe({
            next: (response) => {
                this.bookmarkedMovies = response.data.map((fav: any) => fav.movieId);
                this.updateBookmarkStatus();
            },
            error: (error) => {
                console.error('Hiba a kedvencek betöltésekor:', error);
            }
        });
    }

    updateBookmarkStatus(): void {
        this.isBookmarked = this.selectedMovie ? this.bookmarkedMovies.includes(this.selectedMovie.movieId) : false;
    }

    performSearch(query: string): void {
        this.loading = true;
        this.error = '';
        this.searchService.search(query).subscribe({
            next: (response) => {
                this.searchResults = response.movies;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Hiba történt a keresés során.';
                console.error(err);
                this.loading = false;
                this.searchResults = [];
            }
        });
    }

    showMovieDetails(movieId: number | undefined): void {
        if (movieId === undefined) {
            console.warn('showMovieDetails called with undefined movieId');
            return;
        }
        const foundMovie = this.searchResults.find(m => m.movieId === movieId);
        if (foundMovie) {
            this.selectedMovie = foundMovie;
            this.updateBookmarkStatus();

            if (this.selectedMovie.trailerLink) {
                this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
                    this.selectedMovie.trailerLink
                );
            }

            this.showModal = true;
            this.loadSimilarMovies();
        }
    }

    loadSimilarMovies(): void {
        // Itt implementáld a hasonló filmek lekérését a backendről, ha van ilyen végpont.
        // Jelenleg a newreleaseService-t használjuk dummy adatokhoz, de ezt módosítani kell.
        // Példa (ha lenne searchService.getSimilarMovies):
        // this.searchService.getSimilarMovies(this.selectedMovie.movieId).subscribe({
        //     next: (data) => {
        //         if (Array.isArray(data)) {
        //             this.similarMovies = [...data].sort(() => 0.5 - Math.random()).slice(0, 6);
        //         } else {
        //             console.error('Data is not an array in loadSimilarMovies:', data);
        //             this.similarMovies = [];
        //         }
        //     },
        //     error: (err) => console.error('Hiba a hasonló filmek betöltése során: ', err)
        // });
        // Dummy implementáció a newreleaseService használatával:
        // FIGYELEM: Ez valószínűleg nem a kívánt működés, módosítsd a tényleges API hívásra.
        // Feltételezzük, hogy a newreleaseService visszaad egy filmtömböt.
        this.searchService.search(this.searchQuery).subscribe({
            next: (response) => {
                if (response.movies && Array.isArray(response.movies)) {
                    this.similarMovies = [...response.movies]
                        .filter(movie => movie.movieId !== this.selectedMovie?.movieId) // Ne jelenjen meg az aktuális film
                        .sort(() => 0.5 - Math.random())
                        .slice(0, 6);
                } else {
                    this.similarMovies = [];
                }
            },
            error: (err) => console.error('Hiba a hasonló filmek betöltése során: ', err)
        });
    }

    closeModal(): void {
        this.showModal = false;
        this.selectedMovie = null;
        this.similarMovies = [];
        this.isBookmarked = false;
        this.sanitizedTrailerLink = null;
    }

    toggleSidebar(category: string = ''): void {
        this.selectedCategory = category;
        this.isSidebarOpen = !!category;
        if (category === 'Hasonló filmek' && this.selectedMovie) {
            this.loadSimilarMovies();
        }
    }

    toggleBookmark() {
        if (this.selectedMovie) {
            const movieId = this.selectedMovie.movieId;
            const isCurrentlyBookmarked = this.bookmarkedMovies.includes(movieId);

            if (isCurrentlyBookmarked) {
                this.favoriteService.removeFavorite(movieId).subscribe({
                    next: () => {
                        this.bookmarkedMovies = this.bookmarkedMovies.filter(id => id !== movieId);
                        this.updateBookmarkStatus();
                    },
                    error: (error) => {
                        console.error('Hiba a kedvenc eltávolításakor:', error);
                    }
                });
            } else {
                this.favoriteService.addFavorite(movieId).subscribe({
                    next: () => {
                        this.bookmarkedMovies.push(movieId);
                        this.updateBookmarkStatus();
                    },
                    error: (error) => {
                        console.error('Hiba a kedvenc hozzáadásakor:', error);
                    }
                });
            }
        }
    }
}