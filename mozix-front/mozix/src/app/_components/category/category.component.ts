import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../_services/category.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FavoriteService } from '../../_services/favorite.service';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './category.component.html',
  styleUrl: './category.component.css'
})
export class CategoryComponent implements OnInit {
  mainMovie: any;
  galleryMovies: any[] = [];
  movies: any[] = [];

  mainMovie2: any;
  galleryMovies2: any[] = [];
  movies2: any[] = [];

  mainMovie3: any;
  galleryMovies3: any[] = [];
  movies3: any[] = [];

  selectedMovie: any = null;
  selectedMovie2: any = null;
  selectedMovie3: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  bookmarkedMovies: number[] = [];
  isBookmarked: boolean = false;
  similarMovies: any[] = [];

  constructor(
    private categoryService: CategoryService,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoriteService
  ) { }

  ngOnInit(): void {
    this.loadMovies();
    this.loadMovies2();
    this.loadMovies3();
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
    if (this.selectedMovie) {
      this.isBookmarked = this.bookmarkedMovies.includes(this.selectedMovie.movieId);
    } else if (this.selectedMovie2) {
      this.isBookmarked = this.bookmarkedMovies.includes(this.selectedMovie2.movieId);
    } else if (this.selectedMovie3) {
      this.isBookmarked = this.bookmarkedMovies.includes(this.selectedMovie3.movieId);
    } else {
      this.isBookmarked = false;
    }
  }


  loadMovies(): void {
    this.categoryService.getMovies().subscribe({
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
    this.categoryService.getMovies2().subscribe({
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
    this.categoryService.getMovies3().subscribe({
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

  private selectMovies(): void {
    const shuffled = [...this.movies].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 7);
    this.mainMovie = selected[0];
    this.galleryMovies = selected.slice(1, 7);
  }

  private selectMovies2(): void {
    const shuffled = [...this.movies2].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 7);
    this.mainMovie2 = selected[0];
    this.galleryMovies2 = selected.slice(1, 7);
  }

  private selectMovies3(): void {
    const shuffled = [...this.movies3].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 7);
    this.mainMovie3 = selected[0];
    this.galleryMovies3 = selected.slice(1, 7);
  }


  showMovieDetails(movieId: number | undefined): void {
    console.log('Attempting to show details for movie ID:', movieId);
    if (movieId === undefined) {
      console.warn('showMovieDetails called with undefined movieId');
      return;
    }
    const foundMovie = this.movies.find(m => m.movieId === movieId);
    console.log('Found movie:', foundMovie);

    if (foundMovie) {
      this.selectedMovie = foundMovie;
      this.selectedMovie2 = null;
      this.selectedMovie3 = null;
      this.updateBookmarkStatus();

      if (this.selectedMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies();
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails2(movieId: number | undefined): void {
    console.log('Attempting to show details for movie2 ID:', movieId);
    if (movieId === undefined) {
      console.warn('showMovieDetails2 called with undefined movieId');
      return;
    }
    const foundMovie = this.movies2.find(m => m.movieId === movieId);
    console.log('Found movie2:', foundMovie);

    if (foundMovie) {
      this.selectedMovie2 = foundMovie;
      this.selectedMovie = null;
      this.selectedMovie3 = null;
      this.updateBookmarkStatus();

      if (this.selectedMovie2.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie2.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies();
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails3(movieId: number | undefined): void {
    console.log('Attempting to show details for movie3 ID:', movieId);
    if (movieId === undefined) {
      console.warn('showMovieDetails3 called with undefined movieId');
      return;
    }
    const foundMovie = this.movies3.find(m => m.movieId === movieId);
    console.log('Found movie3:', foundMovie);

    if (foundMovie) {
      this.selectedMovie3 = foundMovie;
      this.selectedMovie = null;
      this.selectedMovie2 = null;
      this.updateBookmarkStatus();

      if (this.selectedMovie3.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie3.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies();
      console.log('Modal should be visible now');
    }
  }


  loadSimilarMovies(): void {
    this.categoryService.getMovies().subscribe({
      next: (data) => {
        if (Array.isArray(data)) {
          this.similarMovies = [...data].sort(() => 0.5 - Math.random()).slice(0, 6);
        } else {
          console.error('Data is not an array in loadSimilarMovies:', data);
          this.similarMovies = [];
        }
      },
      error: (err) => console.error('Hiba a hasonló filmek betöltése során:', err)
    });
  }


  closeModal(): void {
    this.showModal = false;
    this.selectedMovie = null;
    this.selectedMovie2 = null;
    this.selectedMovie3 = null;
    this.sanitizedTrailerLink = null;
    this.similarMovies = [];
    this.isBookmarked = false;
  }


  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && (this.selectedMovie || this.selectedMovie2 || this.selectedMovie3)) {
      this.loadSimilarMovies();
    }
  }

  toggleBookmark() {
    let movieId: number | undefined;
    if (this.selectedMovie) {
      movieId = this.selectedMovie.movieId;
    } else if (this.selectedMovie2) {
      movieId = this.selectedMovie2.movieId;
    } else if (this.selectedMovie3) {
      movieId = this.selectedMovie3.movieId;
    }

    if (movieId) {
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