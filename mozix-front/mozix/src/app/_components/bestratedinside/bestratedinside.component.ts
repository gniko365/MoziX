import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-bestratedinside',
  standalone: true,
  imports: [NavbarComponent, RouterLink, RouterOutlet],
  templateUrl: './bestratedinside.component.html',
  styleUrl: './bestratedinside.component.css'
})
export class BestratedinsideComponent {

}
