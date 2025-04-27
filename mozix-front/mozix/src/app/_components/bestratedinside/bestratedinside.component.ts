import { Component, OnInit, OnDestroy } from '@angular/core';
 import { NavbarComponent } from '../navbar/navbar.component';
 import { RouterLink, RouterOutlet, ActivatedRoute } from '@angular/router';
 import { BestratedService } from '../../_services/bestrated.service';
 import { CommonModule } from '@angular/common';
 import { switchMap, takeUntil, map } from 'rxjs/operators';
 import { Subject, Observable, of } from 'rxjs';
 import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
 import { FavoriteService } from '../../_services/favorite.service'; // Importáld a FavoriteService-t

 interface Movie {
  movieId?: number;
  cover?: string;
  title?: string;
  roundedRating?: number;
  trailerLink?: string;
  // ... egyéb tulajdonságok
 }

 @Component({
  selector: 'app-bestratedinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './bestratedinside.component.html',
  styleUrl: './bestratedinside.component.css'
 })
 export class BestratedinsideComponent implements OnInit, OnDestroy {
  rating: number | null = null;
  movies: Movie[] = [];
  loading = true;
  private ngUnsubscribe = new Subject<void>();

  selectedMovie: any = null;
  selectedMovie2: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  bookmarkedMovies: number[] = []; // Csak az ID-kat tároljuk itt
  isBookmarked: boolean = false;
  similarMovies: Movie[] = []; // Új tömb a hasonló filmek tárolására

  constructor(
   private route: ActivatedRoute,
   private bestratedService: BestratedService,
   private sanitizer: DomSanitizer,
   private favoriteService: FavoriteService // Injectáld a FavoriteService-t
  ) { }

  ngOnInit() {
   console.log('BestratedInsideComponent ngOnInit'); // 1. Ellenőrzés
   this.loadInitialBookmarks(); // Betöltjük a kezdeti könyvjelzőket

   this.route.data.pipe(
    takeUntil(this.ngUnsubscribe),
    switchMap(data => {
     console.log('Route data:', data); // 2. Ellenőrzés
     this.rating = data['rating'];
     console.log('Rating értéke a switchMap-ben:', this.rating); // 3. Ellenőrzés
     this.loading = true;
     return this.loadMoviesByRating(this.rating);
    })
   ).subscribe({
    next: (filteredMovies: any) => {
     this.movies = filteredMovies as Movie[];
     this.loading = false;
     console.log('Betöltött filmek (értékelés szerint):', this.movies);
    },
    error: (err) => {
     console.error('Hiba a filmek betöltésekor:', err);
     this.loading = false;
    }
   });
  }

  loadInitialBookmarks(): void {
   this.favoriteService.listFavorites().pipe(
    takeUntil(this.ngUnsubscribe)
   ).subscribe({
    next: (response) => {
     this.bookmarkedMovies = response.data.map((fav: any) => fav.movieId);
     this.updateBookmarkStatus();
    },
    error: (error) => {
     console.error('Hiba a kedvencek betöltésekor a Legjobbra Értékelt (Belső) oldalon:', error);
    }
   });
  }

  updateBookmarkStatus(): void {
   this.isBookmarked = this.selectedMovie ? this.bookmarkedMovies.includes(this.selectedMovie.movieId) : false;
  }

  toggleBookmark() {
   if (this.selectedMovie) {
    const movieId = this.selectedMovie.movieId;
    const isCurrentlyBookmarked = this.bookmarkedMovies.includes(movieId);

    if (isCurrentlyBookmarked) {
     this.favoriteService.removeFavorite(movieId).pipe(
      takeUntil(this.ngUnsubscribe)
     ).subscribe({
      next: () => {
       this.bookmarkedMovies = this.bookmarkedMovies.filter(id => id !== movieId);
       this.updateBookmarkStatus();
      },
      error: (error) => {
       console.error('Hiba a kedvenc eltávolításakor a Legjobbra Értékelt (Belső) oldalon:', error);
      }
     });
    } else {
     this.favoriteService.addFavorite(movieId).pipe(
      takeUntil(this.ngUnsubscribe)
     ).subscribe({
      next: () => {
       this.bookmarkedMovies.push(movieId);
       this.updateBookmarkStatus();
      },
      error: (error) => {
       console.error('Hiba a kedvenc hozzáadásakor a Legjobbra Értékelt (Belső) oldalon:', error);
      }
     });
    }
   }
  }

  ngOnDestroy() {
   this.ngUnsubscribe.next();
   this.ngUnsubscribe.complete();
  }

  private loadMoviesByRating(rating: number | null): Observable<Movie[]> {
   console.log('loadMoviesByRating meghívva:', rating); // 4. Ellenőrzés
   switch (rating) {
    case 5:
     console.log('getMovies meghívva'); // 5. Ellenőrzés a service hívásakor
     return this.bestratedService.getMovies();
    case 4:
     console.log('getMovies2 meghívva'); // 5. Ellenőrzés a service hívásakor
     return this.bestratedService.getMovies2();
    case 3:
     console.log('getMovies3 meghívva'); // 5. Ellenőrzés a service hívásakor
     return this.bestratedService.getMovies3();
    case 2:
     console.log('getMovies4 meghívva'); // 5. Ellenőrzés a service hívásakor
     return this.bestratedService.getMovies4();
    case 1:
     console.log('getMovies5 meghívva'); // 5. Ellenőrzés a service hívásakor
     return this.bestratedService.getMovies5();
    default:
     return of([]);
   }
  }

  showMovieDetails(movieId: number | undefined): void { // Módosított típus
   if (movieId === undefined) {
    console.warn('showMovieDetails called with undefined movieId');
    return;
   }

   const foundMovie = this.movies.find(m => m.movieId === movieId);

   if (foundMovie) {
    this.selectedMovie = foundMovie;
    this.updateBookmarkStatus(); // Frissítjük a könyvjelző állapotát a kiválasztott film alapján

    if (this.selectedMovie.trailerLink) {
     this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
      this.selectedMovie.trailerLink
     );
    }

    this.showModal = true;
    this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
   } else {
    console.warn('Movie not found with ID:', movieId);
   }
  }

  loadSimilarMovies(): void {
   this.bestratedService.getMovies().pipe( // Használjuk az egyik meglévő service hívást a véletlenszerű filmekhez
    map(data => {
     if (Array.isArray(data)) {
      return [...data]
       .sort(() => 0.5 - Math.random())
       .slice(0, 6); // 6 véletlenszerű film
     } else {
      console.error('Data is not an array in loadSimilarMovies:', data);
      return [];
     }
    }),
    takeUntil(this.ngUnsubscribe)
   ).subscribe({
    next: (similar) => {
     this.similarMovies = similar;
    },
    error: (err) => console.error('Hiba a hasonló filmek betöltése során:', err)
   });
  }

  closeModal(): void {
   this.showModal = false;
   this.selectedMovie = null;
   this.similarMovies = [];
   this.isBookmarked = false; // Reseteljük a könyvjelző állapotát a modal bezárásakor
  }

  toggleSidebar(category: string = ''): void {
   this.selectedCategory = category;
   this.isSidebarOpen = !!category;
   if (category === 'Hasonló filmek' && this.selectedMovie) {
    this.loadSimilarMovies();
   }
  }
 }