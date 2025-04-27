import { Component, OnInit, OnDestroy } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet, ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../_services/category.service';
import { CommonModule } from '@angular/common';
import { switchMap, takeUntil, map } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FavoriteService } from '../../_services/favorite.service';

interface Genre {
  name: string;
}

interface Movie {
  movieId?: number;
  cover?: string;
  title?: string;
  genres?: Genre[];
  trailerLink?: string;
  description?: string;
  length?: number;
  averageRating?: number;
  actors?: any[];
  directors?: any[];
}

@Component({
  selector: 'app-categoryinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './categoryinside.component.html',
  styleUrl: './categoryinside.component.css'
})
export class CategoryInsideComponent implements OnInit, OnDestroy {
  genre: string = '';
  movies: Movie[] = [];
  loading = true;
  private ngUnsubscribe = new Subject<void>();

  selectedMovie: Movie | null = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  bookmarkedMovies: number[] = [];
  isBookmarked: boolean = false;
  similarMovies: Movie[] = [];

  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoriteService
  ) { }

  ngOnInit() {
    this.loadInitialBookmarks();
    this.route.data.pipe(
      takeUntil(this.ngUnsubscribe),
      switchMap(data => {
        this.genre = data['genre'];
        this.loading = true;
        console.log('Betöltés műfaj:', this.genre);
        return this.loadMovies(this.genre);
      })
    ).subscribe({
      next: (allMovies) => {
        this.movies = this.filterMoviesByGenre(allMovies, this.genre);
        this.loading = false;
        console.log('Betöltött filmek:', this.movies);
      },
      error: (err) => {
        console.error('Hiba a filmek betöltésekor:', err);
        this.loading = false;
      }
    });
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
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
    this.isBookmarked = this.selectedMovie ? this.bookmarkedMovies.includes(this.selectedMovie.movieId!) : false;
  }

  private loadMovies(genre: string) {
    switch (genre) {
      case 'Vígjáték':
        return this.categoryService.getVígjátékMovies();
      case 'Dráma':
        return this.categoryService.getDrámaMovies();
      case 'Történelmi':
        return this.categoryService.getHáborúsMovies();
      case 'Háborús':
        return this.categoryService.getWarMovies();
      default:
        return [];
    }
  }

  private filterMoviesByGenre(movies: Movie[], genre: string): Movie[] {
    return movies.filter(movie =>
      movie?.genres?.some(g => g.name === genre)
    );
  }


  showMovieDetails(movieId: number | undefined): void {
    if (movieId === undefined) {
      console.warn('showMovieDetails called with undefined movieId');
      return;
    }
    const foundMovie = this.movies.find(m => m.movieId === movieId);

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
    } else {
      console.warn('Movie not found with ID:', movieId);
    }
  }

  loadSimilarMovies(): void {
    // Here, you might want to fetch similar movies based on the current genre or the selected movie's details.
    // For simplicity, I'm reusing one of the category service methods. Adjust as needed.
    this.categoryService.getVígjátékMovies().pipe(
      takeUntil(this.ngUnsubscribe),
      map(data => {
        if (Array.isArray(data)) {
          return [...data].filter(movie => movie.movieId !== this.selectedMovie?.movieId) // Don't include the selected movie
            .sort(() => 0.5 - Math.random()).slice(0, 6);
        } else {
          console.error('Data is not an array in loadSimilarMovies:', data);
          return [];
        }
      })
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
    this.isBookmarked = false;
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
      const movieId = this.selectedMovie.movieId!;
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