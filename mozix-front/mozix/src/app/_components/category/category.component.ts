import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../_services/category.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

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
  isBookmarked = false;
  similarMovies: any[] = []; // Új tömb a hasonló filmek tárolására

  constructor(private categoryService: CategoryService, private sanitizer: DomSanitizer) {} // Konzisztens név

  ngOnInit(): void {
    this.loadMovies();
    this.loadMovies2();
    this.loadMovies3();
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
    const selected = shuffled.slice(0, 7); // Get first 6 movies
    this.mainMovie = selected[0];
    this.galleryMovies = selected.slice(1, 7); // Get movies 2-6
  }

  private selectMovies2(): void {
    const shuffled = [...this.movies2].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 7); // Get first 6 movies
    this.mainMovie2 = selected[0];
    this.galleryMovies2 = selected.slice(1, 7); // Get movies 2-6
  }

  private selectMovies3(): void {
    const shuffled = [...this.movies3].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 7); // Get first 6 movies
    this.mainMovie3 = selected[0];
    this.galleryMovies3 = selected.slice(1, 7); // Get movies 2-6
  }




  showMovieDetails(movieId: number | undefined): void { // Módosított típus
    console.log('Attempting to show details for movie ID:', movieId);
    if (movieId === undefined) {
      console.warn('showMovieDetails called with undefined movieId');
      return;
    }
    const foundMovie = this.movies.find(m => m.movieId === movieId);
    console.log('Found movie:', foundMovie);

    if (foundMovie) {
      this.selectedMovie = foundMovie;
      this.selectedMovie2 = null; // Clear other movie type

      if (this.selectedMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails2(movieId: number | undefined): void { // Módosított típus
    console.log('Attempting to show details for movie2 ID:', movieId);
    if (movieId === undefined) {
      console.warn('showMovieDetails2 called with undefined movieId');
      return;
    }
    const foundMovie = this.movies2.find(m => m.movieId === movieId);
    console.log('Found movie2:', foundMovie);

    if (foundMovie) {
      this.selectedMovie2 = foundMovie;
      this.selectedMovie = null; // Clear other movie type

      if (this.selectedMovie2.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie2.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails3(movieId: number | undefined): void { // Módosított típus
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

      if (this.selectedMovie3.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie3.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
      console.log('Modal should be visible now');
    }
  }


  loadSimilarMovies(): void {
    this.categoryService.getMovies().subscribe({ // Feltételezzük, hogy ez a service hívás megfelelő a véletlenszerű filmekhez
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
    this.selectedMovie3 = null; // Add this line
    this.sanitizedTrailerLink = null;
    this.similarMovies = []; // Clear similar movies on modal close
  }


  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && (this.selectedMovie || this.selectedMovie2 || this.selectedMovie3)) {
      this.loadSimilarMovies();
    }
  }

  toggleBookmark() {
    this.isBookmarked = !this.isBookmarked;
  }
}