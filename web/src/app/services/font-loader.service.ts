import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

/**
 * Font Loader Service for Onified.ai Application
 * 
 * This service manages font loading detection and provides utilities
 * for progressive font enhancement. It helps improve the user experience
 * by detecting when custom fonts are loaded and applying appropriate styles.
 * 
 * Features:
 * - Font loading detection using FontFace API
 * - Progressive enhancement with fallback fonts
 * - Reactive state management for font loading status
 * - Automatic CSS class application for loaded fonts
 * 
 * @service FontLoaderService
 * @injectable root
 */
@Injectable({
  providedIn: 'root'
})
export class FontLoaderService {
  /** Reactive state for font loading status */
  private fontsLoadedSubject = new BehaviorSubject<boolean>(false);
  public fontsLoaded$ = this.fontsLoadedSubject.asObservable();

  /** List of critical fonts to load */
  private readonly criticalFonts = [
    { family: 'Roboto', weight: '400', style: 'normal' },
    { family: 'Roboto', weight: '500', style: 'normal' },
    { family: 'Roboto', weight: '700', style: 'normal' }
  ];

  /**
   * Constructor - Initializes font loading detection
   */
  constructor() {
    this.initializeFontLoading();
  }

  /**
   * Initializes font loading detection and management
   * @private
   */
  private initializeFontLoading(): void {
    // Check if FontFace API is supported
    if (this.isFontFaceSupported()) {
      this.loadFontsWithAPI();
    } else {
      // Fallback for browsers without FontFace API support
      this.loadFontsWithFallback();
    }
  }

  /**
   * Checks if FontFace API is supported in the current browser
   * @returns boolean indicating FontFace API support
   * @private
   */
  private isFontFaceSupported(): boolean {
    return 'fonts' in document && 'FontFace' in window;
  }

  /**
   * Loads fonts using the modern FontFace API
   * @private
   */
  private loadFontsWithAPI(): void {
    const fontPromises = this.criticalFonts.map(font => {
      try {
        // Create FontFace instance
        const fontFace = new (window as any).FontFace(
          font.family,
          `url(/assets/fonts/Roboto/static/Roboto-${this.getFontFileName(font.weight, font.style)}.ttf)`,
          {
            weight: font.weight,
            style: font.style,
            display: 'swap'
          }
        );

        // Add font to document fonts
        (document.fonts as any).add(fontFace);
        
        // Return loading promise
        return fontFace.load();
      } catch (error) {
        console.warn(`Failed to create FontFace for ${font.family} ${font.weight}:`, error);
        return Promise.resolve();
      }
    });

    // Wait for all critical fonts to load
    Promise.all(fontPromises)
      .then(() => {
        this.onFontsLoaded();
      })
      .catch((error) => {
        console.warn('Some fonts failed to load:', error);
        // Still mark as loaded to prevent indefinite waiting
        this.onFontsLoaded();
      });
  }

  /**
   * Fallback font loading method for older browsers
   * Uses a combination of techniques for better compatibility
   * @private
   */
  private loadFontsWithFallback(): void {
    // Method 1: Check if fonts are already cached
    if (localStorage.getItem('onified-fonts-loaded') === 'true') {
      this.onFontsLoaded();
      return;
    }

    // Method 2: Use document.fonts.ready if available
    if ('fonts' in document && (document.fonts as any).ready) {
      (document.fonts as any).ready.then(() => {
        this.onFontsLoaded();
      }).catch(() => {
        // Fallback to timeout
        this.fallbackTimeout();
      });
    } else {
      // Method 3: Simple timeout fallback
      this.fallbackTimeout();
    }
  }

  /**
   * Fallback timeout method for font loading
   * @private
   */
  private fallbackTimeout(): void {
    setTimeout(() => {
      this.onFontsLoaded();
    }, 3000); // Wait 3 seconds then assume fonts are loaded
  }

  /**
   * Handles the fonts loaded event
   * @private
   */
  private onFontsLoaded(): void {
    // Update reactive state
    this.fontsLoadedSubject.next(true);
    
    // Add CSS class to body for styling
    document.body.classList.add('fonts-loaded');
    
    // Store in localStorage to avoid re-checking on subsequent visits
    localStorage.setItem('onified-fonts-loaded', 'true');
    
    console.log('Onified.ai fonts loaded successfully');
  }

  /**
   * Gets the appropriate font file name based on weight and style
   * @param weight - Font weight (400, 500, 700)
   * @param style - Font style (normal, italic)
   * @returns Font file name without extension
   * @private
   */
  private getFontFileName(weight: string, style: string): string {
    const weightMap: { [key: string]: string } = {
      '300': 'Light',
      '400': 'Regular',
      '500': 'Medium',
      '700': 'Bold'
    };

    const weightName = weightMap[weight] || 'Regular';
    const styleSuffix = style === 'italic' ? 'Italic' : '';
    
    return `${weightName}${styleSuffix}`;
  }

  /**
   * Checks if fonts are currently loaded
   * @returns boolean indicating if fonts are loaded
   */
  public areFontsLoaded(): boolean {
    return this.fontsLoadedSubject.value;
  }

  /**
   * Forces font loading check (useful for manual triggering)
   */
  public checkFontLoading(): void {
    if (this.isFontFaceSupported() && 'fonts' in document) {
      // Check if fonts are ready
      const fonts = document.fonts as any;
      if (fonts.ready) {
        fonts.ready.then(() => {
          this.onFontsLoaded();
        }).catch(() => {
          console.warn('Font ready check failed, using fallback');
          this.onFontsLoaded();
        });
      }
    } else {
      // For browsers without support, just mark as loaded
      this.onFontsLoaded();
    }
  }

  /**
   * Preloads additional font weights/styles on demand
   * @param fonts - Array of font configurations to preload
   * @returns Promise that resolves when fonts are loaded
   */
  public preloadFonts(fonts: Array<{ weight: string; style: string }>): Promise<any> {
    if (!this.isFontFaceSupported()) {
      return Promise.resolve([]);
    }

    const fontPromises = fonts.map(font => {
      try {
        const fontFace = new (window as any).FontFace(
          'Roboto',
          `url(/assets/fonts/Roboto/static/Roboto-${this.getFontFileName(font.weight, font.style)}.ttf)`,
          {
            weight: font.weight,
            style: font.style,
            display: 'swap'
          }
        );

        (document.fonts as any).add(fontFace);
        return fontFace.load();
      } catch (error) {
        console.warn(`Failed to preload font ${font.weight} ${font.style}:`, error);
        return Promise.resolve();
      }
    });

    return Promise.all(fontPromises);
  }

  /**
   * Utility method to create a font loading test element
   * This can be used for more sophisticated font loading detection
   * @private
   */
  private createFontTestElement(): HTMLElement {
    const testElement = document.createElement('div');
    testElement.style.fontFamily = 'Roboto, Arial, sans-serif';
    testElement.style.fontSize = '100px';
    testElement.style.position = 'absolute';
    testElement.style.left = '-9999px';
    testElement.style.top = '-9999px';
    testElement.style.visibility = 'hidden';
    testElement.textContent = 'BESbswy'; // Characters that vary between fonts
    document.body.appendChild(testElement);
    return testElement;
  }

  /**
   * Alternative font loading detection using element width measurement
   * This is a fallback method for browsers with limited FontFace API support
   * @private
   */
  private detectFontLoadingByMeasurement(): void {
    const testElement = this.createFontTestElement();
    const initialWidth = testElement.offsetWidth;
    
    let attempts = 0;
    const maxAttempts = 50; // Maximum 5 seconds (50 * 100ms)
    
    const checkFont = () => {
      attempts++;
      const currentWidth = testElement.offsetWidth;
      
      if (currentWidth !== initialWidth || attempts >= maxAttempts) {
        // Font has loaded (width changed) or timeout reached
        document.body.removeChild(testElement);
        this.onFontsLoaded();
      } else {
        // Check again in 100ms
        setTimeout(checkFont, 100);
      }
    };
    
    // Start checking after a small delay to allow font to start loading
    setTimeout(checkFont, 100);
  }
}