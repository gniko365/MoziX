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


  showMovieDetails(movieId: number): void {
    const foundMovie = this.movies.find(m => m.movieId === movieId);
    
    if (foundMovie) {
      this.selectedMovie = foundMovie;
      
      if (this.selectedMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie.trailerLink
        );
      }
      
      this.showModal = true;
    } else {
      console.warn('Movie not found with ID:', movieId);
    }
  }
  
  closeModal(): void {
    this.showModal = false;
    this.selectedMovie = null;
  }
  
  
  
  
  
  
  
  
  
  
  
  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
  }
  
  toggleBookmark() {
    this.isBookmarked = !this.isBookmarked;
  }
  
}
