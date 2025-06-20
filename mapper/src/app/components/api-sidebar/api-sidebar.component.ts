import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ConfigurationService } from '../../services/configuration.service';
import { DragDropService } from '../../services/drag-drop.service';
import { ApiConfiguration } from '../../models/configuration.interface';

@Component({
  selector: 'app-api-sidebar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './api-sidebar.component.html',
  styleUrl: './api-sidebar.component.scss'
})
export class ApiSidebarComponent implements OnInit, OnDestroy {
  apiConfigurations: ApiConfiguration[] = [];
  filteredApiConfigurations: ApiConfiguration[] = [];
  isLoading = true;
  draggedItem: ApiConfiguration | null = null;
  searchQuery: string = '';
  private subscription = new Subscription();

  constructor(
    private configService: ConfigurationService,
    private dragDropService: DragDropService
  ) {}

  ngOnInit(): void {
    this.subscription.add(
      this.configService.getConfiguration().subscribe(config => {
        if (config) {
          this.apiConfigurations = config.configuration.map(api => ({
            ...api,
            icon: this.getApiIcon(api.api_name),
            color: this.getApiColor(api.api_name),
            description: this.getApiDescription(api.api_name),
            category: this.getApiCategory(api.api_name),
            supportedMethods: ['GET', 'POST', 'PUT', 'DELETE'],
            authTypes: ['none', 'basic', 'bearer', 'apikey']
          }));
          this.filteredApiConfigurations = [...this.apiConfigurations];
          this.isLoading = false;
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onSearchChange(): void {
    if (!this.searchQuery.trim()) {
      this.filteredApiConfigurations = [...this.apiConfigurations];
      return;
    }

    const query = this.searchQuery.toLowerCase();
    this.filteredApiConfigurations = this.apiConfigurations.filter(api => 
      api.api_name.toLowerCase().includes(query) ||
      api.description?.toLowerCase().includes(query) ||
      api.category?.toLowerCase().includes(query)
    );
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.onSearchChange();
  }

  onDragStart(event: DragEvent, apiConfig: ApiConfiguration): void {
    console.log('Drag started for:', apiConfig.api_name);
    console.log('Full API config:', apiConfig);
    
    this.draggedItem = apiConfig;
    
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = 'copy';
      
      // Make sure we're stringifying properly
      const configString = JSON.stringify(apiConfig);
      console.log('Stringified config:', configString);
      
      event.dataTransfer.setData('application/json', configString);
      event.dataTransfer.setData('text/plain', apiConfig.api_name);
    }

    const rect = (event.target as HTMLElement).getBoundingClientRect();
    this.dragDropService.startDrag('node', {
      apiConfig,
      offset: {
        x: event.clientX - rect.left,
        y: event.clientY - rect.top
      }
    }, { x: event.clientX, y: event.clientY });
  }

  onDragEnd(event: DragEvent): void {
    console.log('Drag ended');
    this.draggedItem = null;
    this.dragDropService.endDrag();
  }

  private getApiIcon(apiName: string): string {
    const iconMap: { [key: string]: string } = {
      'manual_trigger': '▶️',
      'jsonplaceholder': '📝',
      'httpbin': '🔧',
      'catfacts': '🐱',
      'reqres': '👤',
      'openai': '🤖'
    };
    return iconMap[apiName] || '🔗';
  }

  private getApiColor(apiName: string): string {
    const colorMap: { [key: string]: string } = {
      'manual_trigger': '#FF6B35',
      'jsonplaceholder': '#4CAF50',
      'httpbin': '#2196F3',
      'catfacts': '#FF9800',
      'reqres': '#9C27B0',
      'openai': '#10A37F'
    };
    return colorMap[apiName] || '#607D8B';
  }

  private getApiDescription(apiName: string): string {
    const descriptionMap: { [key: string]: string } = {
      'manual_trigger': 'Manually trigger workflow execution',
      'jsonplaceholder': 'Fake REST API for testing and prototyping',
      'httpbin': 'HTTP request & response testing service',
      'catfacts': 'Random cat facts API',
      'reqres': 'Hosted REST-API for testing',
      'openai': 'OpenAI GPT API for AI-powered text generation'
    };
    return descriptionMap[apiName] || 'API Integration';
  }

  private getApiCategory(apiName: string): string {
    const categoryMap: { [key: string]: string } = {
      'manual_trigger': 'Trigger',
      'jsonplaceholder': 'API',
      'httpbin': 'API',
      'catfacts': 'API',
      'reqres': 'API',
      'openai': 'AI'
    };
    return categoryMap[apiName] || 'API';
  }
}