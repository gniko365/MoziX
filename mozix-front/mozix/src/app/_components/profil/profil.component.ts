// profil.component.ts
import { Component, HostListener, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet, Router } from '@angular/router';
import { LoginService } from '../../_services/login.service';
import { FavoriteService } from '../../_services/favorite.service'; // Importáld a FavoriteService-t

@Component({
 selector: 'app-profil',
 standalone: true,
 imports: [NavbarComponent, RouterLink, RouterOutlet],
 templateUrl: './profil.component.html',
 styleUrl: './profil.component.css'
})
export class ProfilComponent implements OnInit {
 isDropdownVisible = false;
 username: string | null = null;
 bookmarkedMovies: any[] = []; // Tömb az elmentett filmek részleteinek tárolására
 savedMoviesCount: number = 0;

 constructor(
   private router: Router,
   private loginService: LoginService,
   private favoriteService: FavoriteService // Add hozzá a FavoriteService-t
 ) { }

 ngOnInit(): void {
   this.loadUsername();
   this.loadBookmarkedMovies();
 }

 loadUsername(): void {
   this.username = localStorage.getItem('username');
 }

 loadBookmarkedMovies(): void {
   const storedBookmarks = localStorage.getItem('bookmarkedMovies');
   const bookmarkedMovieIds: number[] = storedBookmarks ? JSON.parse(storedBookmarks) : [];

   this.savedMoviesCount = bookmarkedMovieIds.length;

   // Kérd le az összes elmentett film részleteit
   bookmarkedMovieIds.forEach(movieId => {
     this.favoriteService.getMovie(movieId).subscribe({
       next: (movie) => {
         this.bookmarkedMovies.push(movie);
       },
       error: (error) => {
         console.error('Hiba a kedvenc film betöltésekor:', error);
       }
     });
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
   this.router.navigate(['/mainpage']);
 }
}