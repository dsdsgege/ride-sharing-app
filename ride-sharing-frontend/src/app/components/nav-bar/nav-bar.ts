import { Component } from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {ThemeService} from '../../services/theme-service';
import {faMoon, faSun} from '@fortawesome/free-solid-svg-icons';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [
    FaIconComponent,
    RouterLink
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss'
})
export class NavBar {
  constructor(protected themeService: ThemeService) {
  }


  onThemeButtonClick(): void {
    this.themeService.toggleTheme();
  }

  protected readonly faMoon = faMoon;
  protected readonly faSun = faSun;
}
