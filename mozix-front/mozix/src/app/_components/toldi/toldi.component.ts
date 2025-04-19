import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';



@Component({
  selector: 'app-toldi',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet, CommonModule],
  templateUrl: './toldi.component.html',
  styleUrls: ['./toldi.component.css']
})
export class ToldiComponent  {
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

