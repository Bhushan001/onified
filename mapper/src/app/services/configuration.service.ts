import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, of } from 'rxjs';
import { Configuration, ApiConfiguration } from '../models/configuration.interface';

@Injectable({
  providedIn: 'root'
})
export class ConfigurationService {
  private configurationSubject = new BehaviorSubject<Configuration | null>(null);
  public configuration$ = this.configurationSubject.asObservable();

  private isLoaded = false;

  constructor(private http: HttpClient) {
    this.loadConfiguration();
  }

  /**
   * Load configuration from JSON file
   */
  private loadConfiguration(): void {
    if (this.isLoaded) {
      return;
    }

    this.http.get<Configuration>('assets/mock/input.json')
      .pipe(
        catchError(error => {
          console.error('Error loading configuration:', error);
          return of(null);
        })
      )
      .subscribe(config => {
        this.configurationSubject.next(config);
        this.isLoaded = true;
      });
  }

  /**
   * Get current configuration value
   */
  getCurrentConfiguration(): Configuration | null {
    return this.configurationSubject.value;
  }

  /**
   * Get configuration as Observable
   */
  getConfiguration(): Observable<Configuration | null> {
    return this.configuration$;
  }

  /**
   * Get specific API configuration by name
   */
  getApiConfiguration(apiName: string): ApiConfiguration | null {
    const config = this.getCurrentConfiguration();
    if (!config) {
      return null;
    }
    
    return config.configuration.find(api => api.api_name === apiName) || null;
  }

  /**
   * Get all API configurations
   */
  getAllApiConfigurations(): ApiConfiguration[] {
    const config = this.getCurrentConfiguration();
    return config ? config.configuration : [];
  }

  /**
   * Reload configuration from file
   */
  reloadConfiguration(): void {
    this.isLoaded = false;
    this.loadConfiguration();
  }

  /**
   * Check if configuration is loaded
   */
  isConfigurationLoaded(): boolean {
    return this.isLoaded && this.configurationSubject.value !== null;
  }
}