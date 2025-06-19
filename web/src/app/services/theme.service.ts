import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type Theme = 'light' | 'dark' | 'auto';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'onified-theme';
  private currentTheme = new BehaviorSubject<Theme>('light');
  
  public theme$ = this.currentTheme.asObservable();

  constructor() {
    this.initializeTheme();
  }

  private initializeTheme(): void {
    const savedTheme = localStorage.getItem(this.THEME_KEY) as Theme;
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    if (savedTheme) {
      this.setTheme(savedTheme);
    } else if (prefersDark) {
      this.setTheme('dark');
    } else {
      this.setTheme('light');
    }

    // Listen for system theme changes
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      if (this.currentTheme.value === 'auto') {
        this.applyTheme(e.matches ? 'dark' : 'light');
      }
    });
  }

  public setTheme(theme: Theme): void {
    this.currentTheme.next(theme);
    localStorage.setItem(this.THEME_KEY, theme);
    
    if (theme === 'auto') {
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.applyTheme(prefersDark ? 'dark' : 'light');
    } else {
      this.applyTheme(theme);
    }
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    const body = document.body;
    body.classList.remove('theme-light', 'theme-dark');
    body.classList.add(`theme-${theme}`);
  }

  public getCurrentTheme(): Theme {
    return this.currentTheme.value;
  }

  public toggleTheme(): void {
    const current = this.currentTheme.value;
    if (current === 'light') {
      this.setTheme('dark');
    } else if (current === 'dark') {
      this.setTheme('light');
    } else {
      // If auto, switch to opposite of current system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      this.setTheme(prefersDark ? 'light' : 'dark');
    }
  }
}