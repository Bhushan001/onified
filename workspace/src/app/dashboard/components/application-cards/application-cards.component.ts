import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface Application {
  title: string;
  desc: string;
  icon: string;
  badge?: string;
}

@Component({
  selector: 'app-application-cards',
  standalone: false,
  templateUrl: './application-cards.component.html',
  styleUrls: ['./application-cards.component.scss']
})
export class ApplicationCardsComponent {
  @Input() applications: Application[] = [];
  @Input() showViewAll: boolean = true;
  
  @Output() applicationSelect = new EventEmitter<Application>();
  @Output() viewAll = new EventEmitter<void>();

  onApplicationClick(app: Application): void {
    this.applicationSelect.emit(app);
  }

  onViewAll(): void {
    this.viewAll.emit();
  }
} 