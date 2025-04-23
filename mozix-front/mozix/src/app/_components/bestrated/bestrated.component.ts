import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BestratedService } from '../../_services/bestrated.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-bestrated',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './bestrated.component.html',
  styleUrl: './bestrated.component.css'
})
export class BestratedComponent implements OnInit {
  mainMovie: any;
  galleryMovies: any[] = [];
  movies: any[] = [];

  mainMovie2: any;
  galleryMovies2: any[] = [];
    movies2: any[] = [];

    mainMovie3: any;
  galleryMovies3: any[] = [];
    movies3: any[] = [];

    mainMovie4: any;
  galleryMovies4: any[] = [];
    movies4: any[] = [];

    mainMovie5: any;
  galleryMovies5: any[] = [];
    movies5: any[] = [];


    selectedMovie: any = null;
    selectedMovie2: any = null;
    selectedMovie3: any = null;
    selectedMovie4: any = null;
    selectedMovie5: any = null;
  showModal = false;
  sanitizedTrailerLink: SafeResourceUrl | null = null;
  isSidebarOpen = false;
  selectedCategory = '';
  isBookmarked = false;

  

    constructor(private bestratedService: BestratedService, private sanitizer: DomSanitizer) {} // Konzisztens név

    ngOnInit(): void {
        this.loadMovies();
        this.loadMovies2();
        this.loadMovies3();
        this.loadMovies4();
        this.loadMovies5();
    }
    

    loadMovies(): void {
      this.bestratedService.getMovies().subscribe({
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
      this.bestratedService.getMovies2().subscribe({
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
      this.bestratedService.getMovies3().subscribe({
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

    loadMovies4(): void {
      this.bestratedService.getMovies4().subscribe({
        next: (data) => {
          if (!Array.isArray(data)) {
            console.error('Data is not an array:', data);
            return;
          }
          this.movies4 = data;
          this.selectMovies4();
        },
        error: (err) => console.error('Error loading movies: ', err)
      });
    }

    loadMovies5(): void {
      this.bestratedService.getMovies5().subscribe({
        next: (data) => {
          if (!Array.isArray(data)) {
            console.error('Data is not an array:', data);
            return;
          }
          this.movies5 = data;
          this.selectMovies5();
        },
        error: (err) => console.error('Error loading movies: ', err)
      });
    }

    private selectMovies(): void {
      const shuffled = [...this.movies].sort(() => Math.random() - 0.5);
      const selected = shuffled.slice(0, 5); // Get first 6 movies
      this.mainMovie = selected[0];
      this.galleryMovies = selected.slice(1, 5); // Get movies 2-6
  }

  private selectMovies2(): void {
    const shuffled = [...this.movies2].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 5); // Get first 6 movies
    this.mainMovie2 = selected[0];
    this.galleryMovies2 = selected.slice(1, 5); // Get movies 2-6
  }

  private selectMovies3(): void {
    const shuffled = [...this.movies3].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 5); // Get first 6 movies
    this.mainMovie3 = selected[0];
    this.galleryMovies3 = selected.slice(1, 5); // Get movies 2-6
  }

  private selectMovies4(): void {
    const shuffled = [...this.movies4].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 5); // Get first 6 movies
    this.mainMovie4 = selected[0];
    this.galleryMovies4 = selected.slice(1, 5); // Get movies 2-6
  }

  private selectMovies5(): void {
    const shuffled = [...this.movies5].sort(() => Math.random() - 0.5);
    const selected = shuffled.slice(0, 5); // Get first 6 movies
    this.mainMovie5 = selected[0];
    this.galleryMovies5 = selected.slice(1, 5); // Get movies 2-6
  }









  






  showMovieDetails(movieId: number): void {
    console.log('Attempting to show details for movie ID:', movieId);
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
      console.log('Modal should be visible now');
    }
  }







  showMovieDetails2(movieId: number): void {
    console.log('Attempting to show details for movie2 ID:', movieId);
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
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails3(movieId: number): void {
    console.log('Attempting to show details for movie3 ID:', movieId);
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
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails4(movieId: number): void {
    console.log('Attempting to show details for movie4 ID:', movieId);
    const foundMovie = this.movies4.find(m => m.movieId === movieId);
    console.log('Found movie4:', foundMovie);
    
    if (foundMovie) {
      this.selectedMovie4 = foundMovie;
      this.selectedMovie3 = null;
      this.selectedMovie = null;
      this.selectedMovie2 = null;
      
      if (this.selectedMovie4.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie4.trailerLink
        );
      }

      
      
      this.showModal = true;
      console.log('Modal should be visible now');
    }
  }

  showMovieDetails5(movieId: number): void {
    console.log('Attempting to show details for movie5 ID:', movieId);
    const foundMovie = this.movies5.find(m => m.movieId === movieId);
    console.log('Found movie5:', foundMovie);
    
    if (foundMovie) {
      this.selectedMovie5 = foundMovie;
      this.selectedMovie4 = null;
      this.selectedMovie3 = null;
      this.selectedMovie = null;
      this.selectedMovie2 = null;
      
      if (this.selectedMovie5.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie5.trailerLink
        );
      }

      
      
      this.showModal = true;
      console.log('Modal should be visible now');
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

