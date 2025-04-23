import { Component, OnInit, OnDestroy } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet, ActivatedRoute } from '@angular/router';
import { BestratedService } from '../../_services/bestrated.service';
import { CommonModule } from '@angular/common';
import { switchMap, takeUntil, map } from 'rxjs/operators';
import { Subject, Observable, of } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

interface Movie {
  movieId?: number;
  cover?: string;
  title?: string;
  roundedRating?: number;
  // ... egyéb tulajdonságok
}

@Component({
  selector: 'app-bestratedinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './bestratedinside.component.html',
  styleUrl: './bestratedinside.component.css'
})
export class BestratedinsideComponent implements OnInit, OnDestroy {
  rating: number | null = null;
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
    private bestratedService: BestratedService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    console.log('BestratedInsideComponent ngOnInit'); // 1. Ellenőrzés

    this.route.data.pipe(
      takeUntil(this.ngUnsubscribe),
      switchMap(data => {
        console.log('Route data:', data); // 2. Ellenőrzés
        this.rating = data['rating'];
        console.log('Rating értéke a switchMap-ben:', this.rating); // 3. Ellenőrzés
        this.loading = true;
        return this.loadMoviesByRating(this.rating);
      })
    ).subscribe({
      next: (filteredMovies: any) => {
        this.movies = filteredMovies as Movie[];
        this.loading = false;
        console.log('Betöltött filmek (értékelés szerint):', this.movies);
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

  private loadMoviesByRating(rating: number | null): Observable<Movie[]> {
    console.log('loadMoviesByRating meghívva:', rating); // 4. Ellenőrzés
    switch (rating) {
      case 5:
        console.log('getMovies meghívva'); // 5. Ellenőrzés a service hívásakor
        return this.bestratedService.getMovies();
      case 4:
        console.log('getMovies2 meghívva'); // 5. Ellenőrzés a service hívásakor
        return this.bestratedService.getMovies2();
      case 3:
        console.log('getMovies3 meghívva'); // 5. Ellenőrzés a service hívásakor
        return this.bestratedService.getMovies3();
      case 2:
        console.log('getMovies4 meghívva'); // 5. Ellenőrzés a service hívásakor
        return this.bestratedService.getMovies4();
      case 1:
        console.log('getMovies5 meghívva'); // 5. Ellenőrzés a service hívásakor
        return this.bestratedService.getMovies5();
      default:
        return of([]);
    }
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
