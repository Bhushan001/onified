import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-console',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="console-container">
      <h2>Console Application</h2>
      <p>This is the console application component.</p>
    </div>
  `,
  styles: [`
    .console-container {
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      margin: 10px;
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    h2 {
      color: #333;
      margin-bottom: 10px;
    }
  `]
})
export class ConsoleComponent {
  title = 'console-component';
} 