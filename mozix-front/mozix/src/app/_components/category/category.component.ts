import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../../_services/category.service';

@Component({
  selector: 'app-category',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './category.component.html',
  styleUrl: './category.component.css'
})
export class CategoryComponent implements OnInit {
  mainMovie: any;
  galleryMovies: any[] = [];
    movies: any[] = [];

    mainMovie2: any;
  galleryMovies2: any[] = [];
    movies2: any[] = [];

    mainMovie3: any;
  galleryMovies3: any[] = [];
    movies3: any[] = [];

    constructor(private categoryService: CategoryService) {} // Konzisztens név

    ngOnInit(): void {
        this.loadMovies();
        this.loadMovies2();
        this.loadMovies3();
    }
    

    loadMovies(): void {
      this.categoryService.getMovies().subscribe({
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
      this.categoryService.getMovies2().subscribe({
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
      this.categoryService.getMovies3().subscribe({
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

    private selectMovies(): void {
      const shuffled = [...this.movies].sort(() => Math.random() - 0.5);
      const selected = shuffled.slice(0, 7); // Get first 6 movies
      this.mainMovie = selected[0];
      this.galleryMovies = selected.slice(1, 7); // Get movies 2-6
  }

private selectMovies2(): void {
  const shuffled = [...this.movies2].sort(() => Math.random() - 0.5);
  const selected = shuffled.slice(0, 7); // Get first 6 movies
  this.mainMovie2 = selected[0];
  this.galleryMovies2 = selected.slice(1, 7); // Get movies 2-6
}

private selectMovies3(): void {
  const shuffled = [...this.movies3].sort(() => Math.random() - 0.5);
  const selected = shuffled.slice(0, 7); // Get first 6 movies
  this.mainMovie3 = selected[0];
  this.galleryMovies3 = selected.slice(1, 7); // Get movies 2-6
}
 


}

