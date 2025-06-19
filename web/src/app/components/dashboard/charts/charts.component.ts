import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-charts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="charts-section">
      <div class="section-header">
        <h2 class="section-title">Analytics Overview</h2>
        <div class="time-filter">
          <select class="filter-select">
            <option value="7d">Last 7 days</option>
            <option value="30d" selected>Last 30 days</option>
            <option value="90d">Last 90 days</option>
          </select>
        </div>
      </div>

      <div class="charts-container">
        <!-- Customer Information Chart -->
        <div class="chart-card">
          <div class="chart-header">
            <h3 class="chart-title">Customer Information</h3>
            <div class="chart-value">
              <span class="value-number">2,420</span>
              <span class="value-label">Total Customers</span>
            </div>
          </div>
          <div class="chart-content">
            <canvas #donutChart width="300" height="300"></canvas>
          </div>
          <div class="chart-legend">
            <div class="legend-item" *ngFor="let item of customerData">
              <div class="legend-color" [style.background-color]="item.color"></div>
              <span class="legend-label">{{ item.label }}</span>
              <span class="legend-value">{{ item.value }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./charts.component.scss']
})
export class ChartsComponent implements OnInit, AfterViewInit {
  @ViewChild('donutChart', { static: false }) donutChartRef!: ElementRef<HTMLCanvasElement>;

  customerData = [
    { label: 'New Customers', value: 45, color: '#6366f1' },
    { label: 'Returning Customers', value: 35, color: '#8b5cf6' },
    { label: 'Premium Members', value: 20, color: '#ec4899' }
  ];

  ngOnInit(): void {
    // Component initialization
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.createDonutChart();
    }, 100);
  }

  private createDonutChart(): void {
    const ctx = this.donutChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    const data = this.customerData.map(item => item.value);
    const colors = this.customerData.map(item => item.color);

    this.drawDonutChart(ctx, data, colors);
  }

  private drawDonutChart(ctx: CanvasRenderingContext2D, data: number[], colors: string[]): void {
    const canvas = ctx.canvas;
    const centerX = canvas.width / 2;
    const centerY = canvas.height / 2;
    const radius = Math.min(centerX, centerY) - 30;
    const innerRadius = radius * 0.6;
    
    // Clear canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    
    const total = data.reduce((sum, value) => sum + value, 0);
    let currentAngle = -Math.PI / 2;
    
    // Draw segments
    data.forEach((value, index) => {
      const sliceAngle = (value / total) * 2 * Math.PI;
      
      // Create gradient for each segment
      const gradient = ctx.createRadialGradient(centerX, centerY, innerRadius, centerX, centerY, radius);
      gradient.addColorStop(0, colors[index]);
      gradient.addColorStop(1, this.lightenColor(colors[index], 20));
      
      // Draw slice
      ctx.fillStyle = gradient;
      ctx.beginPath();
      ctx.arc(centerX, centerY, radius, currentAngle, currentAngle + sliceAngle);
      ctx.arc(centerX, centerY, innerRadius, currentAngle + sliceAngle, currentAngle, true);
      ctx.closePath();
      ctx.fill();
      
      // Add subtle shadow
      ctx.shadowColor = 'rgba(0, 0, 0, 0.1)';
      ctx.shadowBlur = 4;
      ctx.shadowOffsetX = 2;
      ctx.shadowOffsetY = 2;
      
      currentAngle += sliceAngle;
    });
    
    // Reset shadow
    ctx.shadowColor = 'transparent';
    ctx.shadowBlur = 0;
    ctx.shadowOffsetX = 0;
    ctx.shadowOffsetY = 0;
    
    // Draw center circle
    ctx.fillStyle = '#ffffff';
    ctx.beginPath();
    ctx.arc(centerX, centerY, innerRadius, 0, 2 * Math.PI);
    ctx.fill();
    
    // Add center text
    ctx.fillStyle = '#374151';
    ctx.font = 'bold 24px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('100%', centerX, centerY - 5);
    
    ctx.font = '14px sans-serif';
    ctx.fillStyle = '#6b7280';
    ctx.fillText('Complete', centerX, centerY + 15);
  }

  private lightenColor(color: string, percent: number): string {
    const num = parseInt(color.replace("#", ""), 16);
    const amt = Math.round(2.55 * percent);
    const R = (num >> 16) + amt;
    const G = (num >> 8 & 0x00FF) + amt;
    const B = (num & 0x0000FF) + amt;
    return "#" + (0x1000000 + (R < 255 ? R < 1 ? 0 : R : 255) * 0x10000 +
      (G < 255 ? G < 1 ? 0 : G : 255) * 0x100 +
      (B < 255 ? B < 1 ? 0 : B : 255)).toString(16).slice(1);
  }
}