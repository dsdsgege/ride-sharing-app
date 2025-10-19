import {Component, inject} from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {ThemeService} from '../../services/theme-service';
import {faMoon, faSun} from '@fortawesome/free-solid-svg-icons';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {KeycloakInstanceService} from '../../services/keycloak-instance-service';

@Component({
  selector: 'app-nav-bar',
  imports: [
    FaIconComponent,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.scss'
})
export class NavBar {

  protected readonly faMoon = faMoon;
  protected readonly faSun = faSun;
  protected readonly keycloakService = inject(KeycloakInstanceService);
  protected readonly keycloakInstance = this.keycloakService.getInstance();

  protected isMenuOpen = false;
  constructor(protected themeService: ThemeService) {
  }

  onThemeButtonClick(): void {
    this.themeService.toggleTheme();
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }

  async logout() {
    await this.keycloakInstance.logout();
  }
}
