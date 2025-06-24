import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface MetricCard {
  title: string;
  value: string;
  subtitle?: string;
  gradient: string;
  textColor: string;
}

@Component({
  selector: 'app-metrics-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="metrics-section">
      <div class="section-header">
        <h2 class="section-title">Overview</h2>
        <div class="section-actions">
          <button class="action-btn">
            <i class="icon-filter">⚙️</i>
            Filter
          </button>
          <button class="action-btn primary">
            <i class="icon-plus">+</i>
            Add New
          </button>
        </div>
      </div>
      
      <div class="metrics-grid">
        <div class="metric-card" 
             *ngFor="let metric of metrics; let i = index"
             [style.background]="metric.gradient"
             [style.color]="metric.textColor">
          <div class="metric-content">
            <div class="metric-value">{{ metric.value }}</div>
            <div class="metric-title">{{ metric.title }}</div>
            <div class="metric-subtitle" *ngIf="metric.subtitle">{{ metric.subtitle }}</div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./metrics-cards.component.scss']
})
export class MetricsCardsComponent {
  metrics: MetricCard[] = [
    {
      title: 'Total Revenue',
      value: '$124.5K',
      subtitle: '+12.5% from last month',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      textColor: 'white'
    },
    {
      title: 'Active Users',
      value: '2,543',
      subtitle: '+8.2% from last week',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      textColor: 'white'
    },
    {
      title: 'Conversion Rate',
      value: '3.45%',
      subtitle: '+0.5% improvement',
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      textColor: 'white'
    },
    {
      title: 'Total Orders',
      value: '1,234',
      subtitle: '+15.3% this month',
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      textColor: 'white'
    },
    {
      title: 'Customer Satisfaction',
      value: '98.5%',
      subtitle: '+2.1% improvement',
      gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
      textColor: 'white'
    },
    {
      title: 'Support Tickets',
      value: '23',
      subtitle: '-15.3% reduction',
      gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
      textColor: '#374151'
    },
    {
      title: 'Page Views',
      value: '45.2K',
      subtitle: '+22.1% this week',
      gradient: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
      textColor: 'white'
    },
    {
      title: 'Bounce Rate',
      value: '2.1%',
      subtitle: '-5.2% improvement',
      gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
      textColor: '#374151'
    }
  ];
}