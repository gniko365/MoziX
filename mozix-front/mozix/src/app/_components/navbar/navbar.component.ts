import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';



@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterOutlet, CommonModule], 
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  activeSidebar: string | null = null;
  showLoginForm: boolean = false;


  toggleForm(): void {
    this.showLoginForm = !this.showLoginForm;
  }

  toggleSidebar(sidebarId: string): void {
    // Ha ugyanaz a sidebar van aktívan, akkor bezárjuk
    if (this.activeSidebar === sidebarId) {
      this.closeSidebar();
    } else {
      this.activeSidebar = sidebarId; // Egyébként aktiváljuk
    }
  }

  closeSidebar(): void {
    this.activeSidebar = null; // Bezárjuk az aktív sidebart
  }

  onRegisterSubmit(event: Event): void {
    event.preventDefault();
    event.preventDefault(); // Megakadályozzuk az alapértelmezett űrlapküldést
  
    const form = event.target as HTMLFormElement;
    const email = (form.querySelector('#email') as HTMLInputElement).value;
    const username = (form.querySelector('#username') as HTMLInputElement).value;
    const password = (form.querySelector('#password') as HTMLInputElement).value;
    const confirmPassword = (form.querySelector('#confirm-password') as HTMLInputElement).value;
    

    console.log('Registration data:', { email, username, password, confirmPassword });
    alert('Sikeres regisztráció!');
    this.closeSidebar();
  }

  onLoginSubmit(event: Event): void {
    event.preventDefault();
    const form = event.target as HTMLFormElement;
    const username = (form.querySelector('#login-username') as HTMLInputElement).value;
    const password = (form.querySelector('#login-password') as HTMLInputElement).value;

    console.log('Login data:', { username, password });
    alert('Sikeres bejelentkezés!');
    this.closeSidebar();
  }
  

}



