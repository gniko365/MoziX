import { Component, OnInit } from '@angular/core';
 import { NavbarComponent } from '../navbar/navbar.component';
 import { RouterLink, RouterOutlet } from '@angular/router';
 import { CommonModule } from '@angular/common';
 import { BestratedService } from '../../_services/bestrated.service';
 import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
 import { FavoriteService } from '../../_services/favorite.service'; // Importáld a FavoriteService-t

 @Component({
  selector: 'app-bestrated',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './bestrated.component.html',
  styleUrl: './bestrated.component.css'
 })
 export class BestratedComponent implements OnInit {
  mainMovie: any;
  galleryMovies: any[] = [];
  movies: any[] = [];

  mainMovie2: any;
  galleryMovies2: any[] = [];
  movies2: any[] = [];

  mainMovie3: any;
  galleryMovies3: any[] = [];
  movies3: any[] = [];

  mainMovie4: any;
  galleryMovies4: any[] = [];
  movies4: any[] = [];

  mainMovie5: any;
  galleryMovies5: any[] = [];
  movies5: any[] = [];

  selectedMovie: any = null;
  selectedMovie2: any = null;
  selectedMovie3: any = null;
  selectedMovie4: any = null;
  selectedMovie5: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  bookmarkedMovies: number[] = []; // Csak az ID-kat tároljuk itt
  isBookmarked: boolean = false;
  similarMovies: any[] = []; // Új tömb a hasonló filmek tárolására


  constructor(
   private bestratedService: BestratedService,
   private sanitizer: DomSanitizer,
   private favoriteService: FavoriteService // Injectáld a FavoriteService-t
  ) { }

  ngOnInit(): void {
   this.loadMovies();
   this.loadMovies2();
   this.loadMovies3();
   this.loadMovies4();
   this.loadMovies5();
   this.loadInitialBookmarks(); // Betöltjük a kezdeti könyvjelzőket
  }

  loadInitialBookmarks(): void {
   this.favoriteService.listFavorites().subscribe({
    next: (response) => {
     this.bookmarkedMovies = response.data.map((fav: any) => fav.movieId);
     this.updateBookmarkStatus();
    },
    error: (error) => {
     console.error('Hiba a kedvencek betöltésekor a Legjobbra Értékelt oldalon:', error);
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
     this.favoriteService.removeFavorite(movieId).subscribe({
      next: () => {
       this.bookmarkedMovies = this.bookmarkedMovies.filter(id => id !== movieId);
       this.updateBookmarkStatus();
      },
      error: (error) => {
       console.error('Hiba a kedvenc eltávolításakor a Legjobbra Értékelt oldalon:', error);
      }
     });
    } else {
     this.favoriteService.addFavorite(movieId).subscribe({
      next: () => {
       this.bookmarkedMovies.push(movieId);
       this.updateBookmarkStatus();
      },
      error: (error) => {
       console.error('Hiba a kedvenc hozzáadásakor a Legjobbra Értékelt oldalon:', error);
      }
     });
    }
   }
  }


  loadMovies(): void {
   this.bestratedService.getMovies().subscribe({
    next: (data) => {
     if (!Array.isArray(data)) {
      console.error('Data is not an array:', data);
      return;
     }
     this.movies = data;
     this.selectMovies();
    },
    error: (err) => console.error('Error loading movies: ', err)
   });
  }

  loadMovies2(): void {
   this.bestratedService.getMovies2().subscribe({
    next: (data) => {
     if (!Array.isArray(data)) {
      console.error('Data is not an array:', data);
      return;
     }
     this.movies2 = data;
     this.selectMovies2();
    },
    error: (err) => console.error('Error loading movies: ', err)
   });
  }

  loadMovies3(): void {
   this.bestratedService.getMovies3().subscribe({
    next: (data) => {
     if (!Array.isArray(data)) {
      console.error('Data is not an array:', data);
      return;
     }
     this.movies3 = data;
     this.selectMovies3();
    },
    error: (err) => console.error('Error loading movies: ', err)
   });
  }

  loadMovies4(): void {
   this.bestratedService.getMovies4().subscribe({
    next: (data) => {
     if (!Array.isArray(data)) {
      console.error('Data is not an array:', data);
      return;
     }
     this.movies4 = data;
     this.selectMovies4();
    },
    error: (err) => console.error('Error loading movies: ', err)
   });
  }

  loadMovies5(): void {
   this.bestratedService.getMovies5().subscribe({
    next: (data) => {
     if (!Array.isArray(data)) {
      console.error('Data is not an array:', data);
      return;
     }
     this.movies5 = data;
     this.selectMovies5();
    },
    error: (err) => console.error('Error loading movies: ', err)
   });
  }

  private selectMovies(): void {
   const shuffled = [...this.movies].sort(() => Math.random() - 0.5);
   const selected = shuffled.slice(0, 5); // Get first 5 movies
   this.mainMovie = selected[0];
   this.galleryMovies = selected.slice(1, 5); // Get movies 2-5
  }

  private selectMovies2(): void {
   const shuffled = [...this.movies2].sort(() => Math.random() - 0.5);
   const selected = shuffled.slice(0, 5); // Get first 5 movies
   this.mainMovie2 = selected[0];
   this.galleryMovies2 = selected.slice(1, 5); // Get movies 2-5
  }

  private selectMovies3(): void {
   const shuffled = [...this.movies3].sort(() => Math.random() - 0.5);
   const selected = shuffled.slice(0, 5); // Get first 5 movies
   this.mainMovie3 = selected[0];
   this.galleryMovies3 = selected.slice(1, 5); // Get movies 2-5
  }

  private selectMovies4(): void {
   const shuffled = [...this.movies4].sort(() => Math.random() - 0.5);
   const selected = shuffled.slice(0, 5); // Get first 5 movies
   this.mainMovie4 = selected[0];
   this.galleryMovies4 = selected.slice(1, 5); // Get movies 2-5
  }

  private selectMovies5(): void {
   const shuffled = [...this.movies5].sort(() => Math.random() - 0.5);
   const selected = shuffled.slice(0, 5); // Get first 5 movies
   this.mainMovie5 = selected[0];
   this.galleryMovies5 = selected.slice(1, 5); // Get movies 2-5
  }


  showMovieDetails(movieId: number): void {
   const foundMovie = this.movies.find(m => m.movieId === movieId);
   this.setSelectedMovie(foundMovie);
  }

  showMovieDetails2(movieId: number): void {
   const foundMovie = this.movies2.find(m => m.movieId === movieId);
   this.setSelectedMovie(foundMovie);
  }

  showMovieDetails3(movieId: number): void {
   const foundMovie = this.movies3.find(m => m.movieId === movieId);
   this.setSelectedMovie(foundMovie);
  }

  showMovieDetails4(movieId: number): void {
   const foundMovie = this.movies4.find(m => m.movieId === movieId);
   this.setSelectedMovie(foundMovie);
  }

  showMovieDetails5(movieId: number): void {
   const foundMovie = this.movies5.find(m => m.movieId === movieId);
   this.setSelectedMovie(foundMovie);
  }

  private setSelectedMovie(movie: any): void {
   if (movie) {
    this.selectedMovie = movie;
    this.selectedMovie2 = null;
    this.selectedMovie3 = null;
    this.selectedMovie4 = null;
    this.selectedMovie5 = null;
    this.updateBookmarkStatus(); // Frissítjük a könyvjelző állapotát a kiválasztott film alapján

    if (this.selectedMovie.trailerLink) {
     this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
      this.selectedMovie.trailerLink
     );
    } else {
     this.sanitizedTrailerLink = null;
    }
    this.showModal = true;
    this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
   }
  }

  loadSimilarMovies(): void {
   this.bestratedService.getMovies().subscribe({ // Használjuk az egyik meglévő service hívást a véletlenszerű filmekhez
    next: (data) => {
     this.similarMovies = [...data]
      .sort(() => 0.5 - Math.random())
      .slice(0, 6); // 6 véletlenszerű film
    },
    error: (err) => console.error('Hiba hasonló filmek betöltése során: ', err)
   });
  }


  closeModal(): void {
   this.showModal = false;
   this.selectedMovie = null;
   this.selectedMovie2 = null;
   this.selectedMovie3 = null;
   this.selectedMovie4 = null;
   this.selectedMovie5 = null;
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