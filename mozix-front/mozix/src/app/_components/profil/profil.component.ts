// profil.component.ts
import { Component, HostListener, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet, Router } from '@angular/router';
import { LoginService } from '../../_services/login.service';
import { FavoriteService } from '../../_services/favorite.service';
import { CommonModule, NgIf } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
// Importáld a FormsModule-ot
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule, NgIf, FormsModule], // Add FormsModule to imports
  templateUrl: './profil.component.html',
  styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit {
  isDropdownVisible = false;
  username: string | null = null;
  bookmarkedMovies: any[] = []; // A kedvenc filmek részleteit tárolja
  savedMoviesCount: number = 0;
  selectedMovie: any = null; // Kiválasztott film a modalhoz
  showModal = false; // Modal megjelenítésének állapota
  sanitizedTrailerLink: SafeResourceUrl | null = null; // Biztonságos trailer link
  similarMovies: any[] = []; // Hasonló filmek
  isSidebarOpen: boolean = false; // Oldalsáv állapota
  selectedCategory: string = ''; // Kiválasztott kategória az oldalsávban
  isBookmarked: boolean = false; // Jelzi, hogy a jelenleg megtekintett film a kedvencek között van-e
  showDeleteConfirmation = false; // Jelzi a jelszó megerősítő ablak láthatóságát
  deletePassword = ''; // A felhasználó által beírt jelszó
  deleteErrorMessage = ''; // Hibaüzenet a törléshez
  userId: string | null = null; // Felhasználói azonosító tárolása a komponensben

  constructor(
    private router: Router,
    private loginService: LoginService,
    private favoriteService: FavoriteService,
    private sanitizer: DomSanitizer // Injectáld a DomSanitizer-t
  ) {
    this.userId = localStorage.getItem('id');
    console.log('UserId a konstruktorban:', this.userId);
  }

  ngOnInit(): void {
    console.log('Profil komponens inicializálódik...');
    this.loadUsername();
    this.loadBookmarkedMoviesWithDetails();
    this.userId = localStorage.getItem('id'); // Javítva: 'id' lekérése
    console.log('UserId az ngOnInit-ban:', this.userId);
  }

  loadUsername(): void {
    this.username = localStorage.getItem('username');
  }

  loadBookmarkedMoviesWithDetails(): void {
    this.favoriteService.listFavorites().subscribe({
      next: (response) => {
        this.bookmarkedMovies = response.data; // Itt már a filmek részleteit kellene kapnod
        this.savedMoviesCount = this.bookmarkedMovies.length;
      },
      error: (error) => {
        console.error('Hiba a kedvenc filmek betöltésekor:', error);
      }
    });
  }

  toggleDropdown() {
    this.isDropdownVisible = !this.isDropdownVisible;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const targetElement = event.target as HTMLElement;
    if (!targetElement.classList.contains('fa-gear') && !targetElement.closest('.dropdown-content')) {
      this.isDropdownVisible = false;
    }
  }

  logout() {
    this.loginService.logout();
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('username');
    localStorage.removeItem('id'); // Javítva: 'id' eltávolítása
    this.router.navigate(['/mainpage']);
  }

  // Új metódus a profil törlésének megerősítéséhez
  confirmDeleteProfile() {
    this.showDeleteConfirmation = true;
  }

  // Metódus a profil törléséhez a jelszó ellenőrzése után
  deleteProfile() {
    const userIdToDelete = localStorage.getItem('id'); // Javítva: 'id' lekérése
    console.log('Törlés kísérlet elején ID (közvetlen):', userIdToDelete, 'jelszó:', this.deletePassword); // Hozzáadva a logolás elején
    console.log('A localStorage tartalma a törlés előtt:', localStorage); // ÚJ SOR
    if (!userIdToDelete) {
      console.error('Felhasználói azonosító nem található.');
      return;
    }
    this.loginService.deleteUser(userIdToDelete, this.deletePassword).subscribe({
      next: (response) => {
        console.log('Sikeres profil törlés:', response);
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('username');
        localStorage.removeItem('id'); // Javítva: 'id' eltávolítása
        this.router.navigate(['/mainpage']); // Átirányítás a főoldalra
        this.closeDeleteConfirmation(); // Bezárjuk a megerősítő ablakot sikeres törlés esetén
      },
      error: (error) => {
        console.error('Hiba a profil törlésekor:', error);
        this.deleteErrorMessage = 'Hibás jelszó vagy a törlés nem sikerült.';
        // NE zárjuk be a modalt hiba esetén, hogy a felhasználó láthassa az üzenetet
      }
    });
  }

  closeDeleteConfirmation() {
    this.showDeleteConfirmation = false;
    this.deletePassword = '';
    this.deleteErrorMessage = '';
  }

  updateDeletePassword(event: any) {
    this.deletePassword = event.target.value;
  }

  togglePasswordVisibility(inputId: string, eyeIconId: string): void {
    const passwordInput = document.getElementById(inputId) as HTMLInputElement;
    const eyeIcon = document.getElementById(eyeIconId) as HTMLElement;

    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      if (eyeIcon) {
        eyeIcon.classList.remove('fa-eye');
        eyeIcon.classList.add('fa-eye-slash');
      }
    } else {
      passwordInput.type = 'password';
      if (eyeIcon) {
        eyeIcon.classList.remove('fa-eye-slash');
        eyeIcon.classList.add('fa-eye');
      }
    }
  }

  showMovieDetails(movie: any): void { // A paraméter most egy film objektum
    this.selectedMovie = movie;
    this.updateBookmarkStatus();
    if (this.selectedMovie.trailerLink) {
      this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
        this.selectedMovie.trailerLink
      );
    }
    this.showModal = true;
    this.loadSimilarMovies(); // Hasonló filmek betöltése a modal megnyitásakor
  }

  updateBookmarkStatus(): void {
    this.isBookmarked = this.selectedMovie ? this.bookmarkedMovies.some(fav => fav.movieId === this.selectedMovie.movieId) : false;
  }

  toggleBookmark() {
    if (this.selectedMovie) {
      const movieId = this.selectedMovie.movieId;
      const isCurrentlyBookmarked = this.bookmarkedMovies.some(fav => fav.movieId === movieId);

      if (isCurrentlyBookmarked) {
        this.favoriteService.removeFavorite(movieId).subscribe({
          next: () => {
            this.bookmarkedMovies = this.bookmarkedMovies.filter(fav => fav.movieId !== movieId);
            this.savedMoviesCount = this.bookmarkedMovies.length;
            this.updateBookmarkStatus();
          },
          error: (error) => {
            console.error('Hiba a kedvenc eltávolításakor:', error);
          }
        });
      } else {
        this.favoriteService.addFavorite(movieId).subscribe({
          next: () => {
            // Ha sikeresen hozzáadtuk, frissítsd a kedvencek listáját
            this.favoriteService.listFavorites().subscribe({
              next: (updatedResponse) => {
                this.bookmarkedMovies = updatedResponse.data;
                this.savedMoviesCount = this.bookmarkedMovies.length;
                this.updateBookmarkStatus();
              },
              error: (error) => {
                console.error('Hiba a kedvencek frissítésekor:', error);
              }
            });
          },
          error: (error) => {
            console.error('Hiba a kedvenc hozzáadásakor:', error);
          }
        });
      }
    }
  }

  loadSimilarMovies(): void {
    // Itt valószínűleg egy olyan endpointot kellene meghívnod, ami az adott filmhez hasonló filmeket ad vissza.
    // Jelenleg a getRandomMovies-t hívod, ami nem biztos, hogy a legjobb megoldás.
    this.favoriteService.listFavorites().subscribe({ // Például a kedvencek közül válogatunk hasonlókat
      next: (data) => {
        this.similarMovies = [...data.data]
          .filter(movie => movie.movieId !== this.selectedMovie?.movieId) // Ne jelenjen meg maga a film
          .sort(() => 0.6 - Math.random())
          .slice(0, 6);
      },
      error: (err) => console.error('Hiba a hasonló filmek betöltése során: ', err)
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedMovie = null;
    this.sanitizedTrailerLink = null;
    this.similarMovies = [];
    this.isSidebarOpen = false;
    this.selectedCategory = '';
    this.isBookmarked = false;
  }

  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && this.selectedMovie) {
      this.loadSimilarMovies();
    }
  }
}