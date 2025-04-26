import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginService, LoginResponse } from '../../_services/login.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterOutlet, CommonModule, FormsModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  activeSidebar: string | null = null;
  showLoginForm: boolean = false;
  loginCredentials = { username: '', password: '' }; // Felhasználónévre változtatva
  errorMessage = '';
  isLoggedIn = false;
  registrationData = { email: '', username: '', password: '', confirmPassword: '' };
  registrationErrorMessage = '';
  passwordVisible = false;
  confirmPasswordVisible = false;
  loginPasswordVisible = false;
  readonly emailRegex: RegExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  constructor(private router: Router, private loginService: LoginService) { }

  ngOnInit(): void {
    this.checkLoginStatus();
  }

  checkLoginStatus(): void {
    const token = localStorage.getItem('jwtToken');
    this.isLoggedIn = !!token;
  }

  toggleForm(): void {
    this.showLoginForm = !this.showLoginForm;
    this.errorMessage = '';
    this.registrationErrorMessage = '';
  }

  toggleSidebar(sidebarId: string): void {
    if (this.activeSidebar === sidebarId) {
      this.closeSidebar();
    } else {
      this.activeSidebar = sidebarId;
      this.errorMessage = '';
      this.registrationErrorMessage = '';
    }
  }

  closeSidebar(): void {
    this.activeSidebar = null;
    this.errorMessage = '';
    this.registrationErrorMessage = '';
  }

  onRegisterSubmit(event: Event): void {
    event.preventDefault();
    this.registrationErrorMessage = '';

    if (!this.emailRegex.test(this.registrationData.email)) {
      this.registrationErrorMessage = 'Kérlek, érvényes email címet adj meg.';
      return;
    }

    if (this.registrationData.password !== this.registrationData.confirmPassword) {
      this.registrationErrorMessage = 'A jelszavak nem egyeznek.';
      return;
    }

    if (this.registrationData.password.length < 8) {
      this.registrationErrorMessage = 'A jelszónak legalább 8 karakter hosszúnak kell lennie.';
      return;
    }

    if (!/\d/.test(this.registrationData.password)) {
      this.registrationErrorMessage = 'A jelszónak tartalmaznia kell legalább egy számot.';
      return;
    }

    if (!/[^a-zA-Z0-9\s]/.test(this.registrationData.password)) {
      this.registrationErrorMessage = 'A jelszónak tartalmaznia kell legalább egy speciális karaktert.';
      return;
    }

    const registerPayload = {
      email: this.registrationData.email,
      username: this.registrationData.username,
      password: this.registrationData.password
    };

    this.loginService.register(registerPayload).subscribe({
      next: (response) => {
        if (response.status === 'success' && response.statusCode === 201) {
          alert(response.message);
          localStorage.setItem('username', this.registrationData.username);
          this.showLoginForm = true;
        } else if (response.status === 'error' && response.statusCode === 409) {
          this.registrationErrorMessage = response.message;
        } else {
          this.registrationErrorMessage = 'Sikertelen regisztráció. Ismeretlen hiba.';
          console.error('Ismeretlen regisztrációs hiba:', response);
        }
      },
      error: (error) => {
        console.error('Hiba a regisztráció során:', error);
        this.registrationErrorMessage = 'Hiba a regisztráció során. Kérlek, próbáld újra később.';
      }
    });
  }

  onLoginSubmit(): void {
    this.errorMessage = '';
    this.loginService.login(this.loginCredentials).subscribe({
      next: (response: LoginResponse) => {
        if (response.status === 'success' && response.statusCode === 200 && response.jwt) {
          this.isLoggedIn = true;
          localStorage.setItem('jwtToken', response.jwt);
          localStorage.setItem('username', this.loginCredentials.username); // Felhasználónév mentése
          console.log('Sikeres bejelentkezés', response);
          this.closeSidebar();
          this.router.navigate(['/mainpage']);
        } else if (response.status === 'error' && response.statusCode === 401) {
          this.errorMessage = response.message;
          this.isLoggedIn = false;
        } else {
          this.errorMessage = 'Sikertelen bejelentkezés. Ismeretlen hiba.';
          console.error('Ismeretlen bejelentkezési hiba:', response);
          this.isLoggedIn = false;
        }
      },
      error: (error) => {
        console.error('Hiba a bejelentkezés során:', error);
        this.errorMessage = 'Hiba a bejelentkezés során. Kérlek, próbáld újra később.';
        this.isLoggedIn = false;
      }
    });
  }

  togglePasswordVisibility(): void {
    this.passwordVisible = !this.passwordVisible;
  }

  toggleConfirmPasswordVisibility(): void {
    this.confirmPasswordVisible = !this.confirmPasswordVisible;
  }

  toggleLoginPasswordVisibility(): void {
    this.loginPasswordVisible = !this.loginPasswordVisible;
  }
}