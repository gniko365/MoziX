import { Component, OnInit } from '@angular/core';
 import { NavbarComponent } from '../navbar/navbar.component';
 import { RouterLink, RouterOutlet } from '@angular/router';
 import { MainpageService } from '../../_services/mainpage.service';
 import { CommonModule } from '@angular/common';
 import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
 import { FavoriteService } from '../../_services/favorite.service'; // Importáld a FavoriteService-t

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
  bookmarkedMovies: number[] = []; // Tömb az elmentett filmek azonosítóinak tárolására
  isBookmarked: boolean = false; // Hozzáadtam ezt a sort

  constructor(
    private mainpageService: MainpageService,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoriteService // Add hozzá a FavoriteService-t
  ) { }

  ngOnInit(): void {
    this.loadMovies();
    this.startCarousel();
    this.loadBookmarkedMovies(); // Töltsd be az elmentett filmeket a localStorage-ból
  }

  loadBookmarkedMovies(): void {
    const storedBookmarks = localStorage.getItem('bookmarkedMovies');
    this.bookmarkedMovies = storedBookmarks ? JSON.parse(storedBookmarks) : [];
    this.updateBookmarkStatus(); // Ellenőrizzük a kezdeti könyvjelző állapotot
  }

  saveBookmarkedMovies(): void {
    localStorage.setItem('bookmarkedMovies', JSON.stringify(this.bookmarkedMovies));
    this.updateBookmarkStatus(); // Frissítjük a könyvjelző állapotát mentés után
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
            this.saveBookmarkedMovies();
          },
          error: (error) => {
            console.error('Hiba a kedvenc eltávolításakor:', error);
          }
        });
      } else {
        this.favoriteService.addFavorite(movieId).subscribe({
          next: () => {
            this.bookmarkedMovies.push(movieId);
            this.saveBookmarkedMovies();
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
      this.updateBookmarkStatus(); // Frissítjük a könyvjelző állapotát a film részleteinek megjelenítésekor

      if (this.selectedMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies(); // Hívjuk meg a hasonló filmek betöltését
    }
  }

  loadSimilarMovies(): void {
    this.mainpageService.getRandomMovies().subscribe({
      next: (data) => {
        this.similarMovies = [...data]
          .sort(() => 0.6 - Math.random())
          .slice(0, 6); // Például 5 hasonló filmet kérünk le
      },
      error: (err) => console.error('Hiba hasonló filmek betöltése során: ', err)
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedMovie = null;
    this.similarMovies = []; // Töröljük a hasonló filmeket a modal bezárásakor
    this.isBookmarked = false; // Reseteljük a könyvjelző állapotát a modal bezárásakor
  }



  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && this.selectedMovie) {
      this.loadSimilarMovies(); // Betöltjük a hasonló filmeket, ha az oldalsáv nyílik és van kiválasztott film
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
 }