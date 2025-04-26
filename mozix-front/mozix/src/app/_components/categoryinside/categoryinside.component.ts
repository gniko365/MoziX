import { Component, OnInit, OnDestroy } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet, ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../_services/category.service';
import { CommonModule } from '@angular/common';
import { switchMap, takeUntil, map } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

interface Genre {
  name: string;
}

interface Movie {
  movieId?: number;
  cover?: string;
  title?: string;
  genres?: Genre[];
  trailerLink?: string;
}

@Component({
  selector: 'app-categoryinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './categoryinside.component.html',
  styleUrl: './categoryinside.component.css'
})
export class CategoryInsideComponent implements OnInit, OnDestroy {
  genre: string = ''; // Maradjon sima string
  movies: Movie[] = [];
  loading = true;
  private ngUnsubscribe = new Subject<void>();

  selectedMovie: any = null;
  selectedMovie2: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  isBookmarked = false;
  similarMovies: Movie[] = []; // Új tömb a hasonló filmek tárolására

  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.route.data.pipe(
      takeUntil(this.ngUnsubscribe),
      switchMap(data => {
        this.genre = data['genre']; // Állítsd be a genre-t itt
        this.loading = true;
        console.log('Betöltés műfaj:', this.genre);
        return this.loadMovies(this.genre);
      })
    ).subscribe({
      next: (allMovies) => {
        this.movies = this.filterMoviesByGenre(allMovies, this.genre); // Használd a this.genre-t
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


  showMovieDetails(movieId: number | undefined): void { // Módosított típus
    if (movieId === undefined) {
      console.warn('showMovieDetails called with undefined movieId');
      return;
    }
    const foundMovie = this.movies.find(m => m.movieId === movieId);

    if (foundMovie) {
      this.selectedMovie = foundMovie;

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
    this.categoryService.getVígjátékMovies().pipe( // Példaként a vígjátékokat használjuk, érdemes lehet a kategóriához illőt vagy egy általánosat használni
      takeUntil(this.ngUnsubscribe),
      map(data => {
        if (Array.isArray(data)) {
          return [...data].sort(() => 0.5 - Math.random()).slice(0, 6);
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
  }


  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && this.selectedMovie) {
      this.loadSimilarMovies();
    }
  }

  toggleBookmark() {
    this.isBookmarked = !this.isBookmarked;
  }
}