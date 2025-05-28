import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MainpageService } from '../../_services/mainpage.service';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FavoriteService } from '../../_services/favorite.service';
import { MovieService } from '../../_services/movie.service';

@Component({
  selector: 'app-mainpage',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './mainpage.component.html',
  styleUrls: ['./mainpage.component.css']
})
export class MainpageComponent implements OnInit {
  movies: any[] = [];
  randomMovies: any[] = [];
  currentSlideIndex: number = 0;
  selectedMovie: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  similarMovies: any[] = [];
  isSidebarOpen: boolean = false;
  selectedCategory: string = '';
  bookmarkedMovies: number[] = [];
  isBookmarked: boolean = false;
  isAdminUser: boolean = false; 

  constructor(
    private mainpageService: MainpageService,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoriteService,
    private movieService: MovieService 
  ) { }

  ngOnInit(): void {
    this.loadMovies();
    this.startCarousel();
    this.loadInitialBookmarks();
    this.checkAdminStatus(); 
  }


  getJwtToken(): string | null {
    return localStorage.getItem('jwtToken'); 
  }

  
  checkAdminStatus(): void {
    const token = this.getJwtToken();
   
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.isAdminUser = payload?.role === 'admin'; 
      } catch (error) {
        console.error('Hiba a JWT dekódolásakor:', error);
        this.isAdminUser = false;
      }
    } else {
      this.isAdminUser = false;
    }
    console.log('Admin user:', this.isAdminUser);
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

  loadMovies(): void {
    this.mainpageService.getRandomMovies().subscribe({
      next: (data) => {
        this.movies = data;
        this.selectRandomMovies();
      },
      error: (err) => console.error('Hiba filmek betöltése során: ', err)
    });
  }

  private selectRandomMovies(): void {
    this.randomMovies = [...this.movies]
      .sort(() => 0.5 - Math.random())
      .slice(0, 3);
  }

  showMovieDetails(movieId: number): void {
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
    }
  }

  loadSimilarMovies(): void {
    this.mainpageService.getRandomMovies().subscribe({
      next: (data) => {
        this.similarMovies = [...data]
          .sort(() => 0.6 - Math.random())
          .slice(0, 6);
      },
      error: (err) => console.error('Hiba hasonló filmek betöltése során: ', err)
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

  startCarousel(): void {
    setInterval(() => {
      this.moveSlide(1);
    }, 5000);
  }

  moveSlide(step: number): void {
    const carousel = document.querySelector('.carousel') as HTMLElement;
    const slides = document.querySelectorAll('.carousel-slide');
    const totalSlides = slides.length;

    if (!slides.length) return;

    this.currentSlideIndex = (this.currentSlideIndex + step + totalSlides) % totalSlides;

    carousel.style.transform = `translateX(-${this.currentSlideIndex * 100}%)`;
  }

  deleteThisMovie(): void {
    if (this.selectedMovie && this.isAdminUser) {
      const movieId = this.selectedMovie.movieId;
      const token = this.getJwtToken();
      if (token) {
        this.movieService.deleteMovie(movieId, token).subscribe({
          next: (response) => {
            console.log('Film sikeresen törölve:', response);
            this.closeModal();
            this.loadMovies();
          },
          error: (error) => {
            console.error('Hiba a film törlése során:', error);
          }
        });
      } else {
        console.error('Nincs JWT token.');
      }
    }
  }
}