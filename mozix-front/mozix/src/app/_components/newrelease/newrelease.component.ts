import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NewreleaseService } from '../../_services/newrelease.service';
import { MainpageService } from '../../_services/mainpage.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
    selector: 'app-newrelease', // Konzisztens név
    standalone: true,
    imports: [
        NavbarComponent, 
        RouterLink, 
        RouterOutlet, 
        CommonModule,
    ],
    templateUrl: './newrelease.component.html',
    styleUrl: './newrelease.component.css' // Javított elérési út
})
export class NewreleaseComponent implements OnInit { // Implementálja az OnInit-et
    latestMovies: any[] = [];
    selectedLatestMovie: any = null;
    sanitizedTrailerLink: SafeResourceUrl | null = null;
    showModal = false;

    constructor(private newreleaseService: NewreleaseService, mainpageService: MainpageService, private sanitizer: DomSanitizer) {} // Konzisztens név

    ngOnInit(): void {
        this.loadLatestMovies();
    }

    loadLatestMovies(): void {
        this.newreleaseService.getLatestMovies().subscribe({
            next: (data) => { // Javított nyíl függvény szintaxis
                this.latestMovies = data;
                this.selectLatestMovies();
            },
            error: (err) => console.error('Hiba filmek betöltése során: ', err) // Javított nyíl függvény
        });
    }

    private selectLatestMovies(): void {
        // Véletlenszerű rendezés és az első 8 elem kiválasztása
        this.latestMovies = [...this.latestMovies]
            
            .slice(0, 8);
    }

















    showMovieDetails(movieId: number): void {
        const foundMovie = this.latestMovies.find(m => m.movieId === movieId);
        if (foundMovie) {
          this.selectedLatestMovie = foundMovie;
          
          if (this.selectedLatestMovie.trailerLink) {
            this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
              this.selectedLatestMovie.trailerLink
            );
          }
          
          this.showModal = true;
        }
      }
    
      closeModal(): void {
        this.showModal = false;
        this.selectedLatestMovie = null;
      }
    
    
    
      isSidebarOpen: boolean = false;
      selectedCategory: string = '';
      isBookmarked: boolean = false; 
    
      toggleSidebar(category: string = ''): void {
        this.selectedCategory = category;
        this.isSidebarOpen = !!category;
      }
      
      toggleBookmark() {
        this.isBookmarked = !this.isBookmarked;
    }
    
}
