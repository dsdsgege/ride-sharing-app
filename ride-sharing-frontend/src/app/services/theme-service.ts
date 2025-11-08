import {Injectable, Renderer2, RendererFactory2} from '@angular/core';

const DARK_THEME_CLASS = 'dark-theme';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private renderer: Renderer2;
  private isDark = localStorage.getItem('theme') === 'dark';

  constructor(rendererFactory: RendererFactory2) {
    this.renderer = rendererFactory.createRenderer(null, null);

    if(this.isDark) {
      this.renderer.addClass(document.body, DARK_THEME_CLASS);
    }
  }

  toggleTheme(): void {
    this.isDark = !this.isDark;
    localStorage.setItem('theme', this.isDark ? 'dark' : 'light');

    if(this.isDark) {
      this.renderer.addClass(document.body, DARK_THEME_CLASS);
    } else {
      this.renderer.removeClass(document.body, DARK_THEME_CLASS);
    }
  }

  isDarkMode(): boolean {
    return this.isDark;
  }
}
