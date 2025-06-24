import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './services/theme.service';
import { FontLoaderService } from './services/font-loader.service';

/**
 * Root component of the Onified.ai application
 * 
 * This component serves as the main entry point for the Angular application.
 * It initializes core services including theme management and font loading
 * for optimal user experience.
 * 
 * Features:
 * - Theme service initialization
 * - Font loading management
 * - Router outlet for navigation
 * - Progressive enhancement support
 * 
 * @component AppComponent
 * @implements OnInit
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  /** Application title used for display purposes */
  title = 'onified-app';

  /**
   * Constructor - Injects required services
   * @param themeService - Service for managing application themes
   * @param fontLoaderService - Service for managing font loading
   */
  constructor(
    private themeService: ThemeService,
    private fontLoaderService: FontLoaderService
  ) {}

  /**
   * Component initialization lifecycle hook
   * 
   * Initializes core application services:
   * - Theme service applies appropriate theme based on user preferences
   * - Font loader service manages progressive font loading
   */
  ngOnInit(): void {
    // Theme service will initialize automatically through its constructor
    // No additional initialization needed here
    
    // Font loader service will also initialize automatically
    // Subscribe to font loading status for potential UI updates
    this.fontLoaderService.fontsLoaded$.subscribe(loaded => {
      if (loaded) {
        console.log('Application fonts are ready');
        // You can add any additional logic here when fonts are loaded
        // For example, triggering animations or layout adjustments
      }
    });
  }
}