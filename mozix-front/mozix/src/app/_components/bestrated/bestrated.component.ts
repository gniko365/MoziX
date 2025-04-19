import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BestratedService } from '../../_services/bestrated.service';

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

    constructor(private bestratedService: BestratedService) {} // Konzisztens név

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
}
