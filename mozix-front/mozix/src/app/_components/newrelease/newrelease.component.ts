import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NewreleaseService } from '../../_services/newrelease.service';
import { MainpageService } from '../../_services/mainpage.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { FavoriteService } from '../../_services/favorite.service';

@Component({
  selector: 'app-newrelease',
  standalone: true,
  imports: [
    NavbarComponent,
    RouterLink,
    RouterOutlet,
    CommonModule,
  ],
  templateUrl: './newrelease.component.html',
  styleUrl: './newrelease.component.css'
})
export class NewreleaseComponent implements OnInit {
  latestMovies: any[] = [];
  selectedLatestMovie: any = null;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  showModal = false;
  isSidebarOpen: boolean = false;
  selectedCategory: string = '';
  bookmarkedMovies: number[] = [];
  isBookmarked: boolean = false;
  similarMovies: any[] = [];
  selectedMovieId: number | null = null;

  constructor(
    private newreleaseService: NewreleaseService,
    private mainpageService: MainpageService,
    private sanitizer: DomSanitizer,
    private favoriteService: FavoriteService
  ) { }

  ngOnInit(): void {
    this.loadLatestMovies();
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
    this.isBookmarked = this.selectedLatestMovie ? this.bookmarkedMovies.includes(this.selectedLatestMovie.movieId) : false;
  }


  loadLatestMovies(): void {
    this.newreleaseService.getLatestMovies().subscribe({
      next: (data) => {
        this.latestMovies = data;
        this.selectLatestMovies();
      },
      error: (err) => console.error('Hiba filmek betöltése során: ', err)
    });
  }

  private selectLatestMovies(): void {
    this.latestMovies = [...this.latestMovies]
      .sort(() => 0.5 - Math.random())
      .slice(0, 8);
  }


  showMovieDetails(movieId: number | undefined): void {
    if (movieId === undefined) {
      console.warn('showMovieDetails called with undefined movieId');
      return;
    }
    const foundMovie = this.latestMovies.find(m => m.movieId === movieId);
    if (foundMovie) {
      this.selectedLatestMovie = foundMovie;
      this.updateBookmarkStatus();

      if (this.selectedLatestMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedLatestMovie.trailerLink
        );
      }

      this.showModal = true;
      this.loadSimilarMovies();
    }
  }

  loadSimilarMovies(): void {
    this.newreleaseService.getLatestMovies().subscribe({
      next: (data) => {
        if (Array.isArray(data)) {
          this.similarMovies = [...data].sort(() => 0.5 - Math.random()).slice(0, 6);
        } else {
          console.error('Data is not an array in loadSimilarMovies:', data);
          this.similarMovies = [];
        }
      },
      error: (err) => console.error('Hiba a hasonló filmek betöltése során: ', err)
    });
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedLatestMovie = null;
    this.similarMovies = [];
    this.isBookmarked = false;
  }


  toggleSidebar(category: string = ''): void {
    this.selectedCategory = category;
    this.isSidebarOpen = !!category;
    if (category === 'Hasonló filmek' && this.selectedLatestMovie) {
      this.loadSimilarMovies();
    }
  }

  toggleBookmark() {
    if (this.selectedLatestMovie) {
      const movieId = this.selectedLatestMovie.movieId;
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