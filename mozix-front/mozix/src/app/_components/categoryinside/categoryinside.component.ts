import { Component, OnInit } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { CategoryService } from '../../_services/category.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-categoryinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './categoryinside.component.html',
  styleUrl: './categoryinside.component.css'
})
export class CategoryInsideComponent implements OnInit {
  genre: string = '';
  movies: any[] = [];
  loading = true;


  
  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService
  ) {}


  
  ngOnInit() {
    console.log('Initializing CategoryInsideComponent');
    
    this.route.data.subscribe(data => {
      this.genre = data['genre'];
      console.log('Received genre:', this.genre);
      
      this.loadMovies();
    });
  }
  
  private loadMovies() {
    console.log('Loading movies for genre:', this.genre);
    
    this.categoryService.getComedyMovies().subscribe({
      next: (movies) => {
        console.log('Received movies:', movies);
        this.movies = movies;
      },
      error: (err) => console.error('Error:', err)
    });
  }
}