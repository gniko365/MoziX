import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { MainpageService } from '../../_services/mainpage.service';
import { CommonModule } from '@angular/common';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

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
  
  

  constructor(private mainpageService: MainpageService, private sanitizer: DomSanitizer) {}
  

  ngOnInit(): void {
    this.loadMovies();
    this.startCarousel();
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
      
      if (this.selectedMovie.trailerLink) {
        this.sanitizedTrailerLink = this.sanitizer.bypassSecurityTrustResourceUrl(
          this.selectedMovie.trailerLink
        );
      }
      
      this.showModal = true;
    }
  }

  closeModal(): void {
    this.showModal = false;
    this.selectedMovie = null;
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
