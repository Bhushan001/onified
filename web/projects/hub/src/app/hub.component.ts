import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-hub',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="hub-container">
      <h2>Hub Application</h2>
      <p>This is the hub application loaded as a micro-frontend.</p>
      <div class="hub-content">
        <p>Hub is successfully loaded through the shell application!</p>
        <p>You can now add your hub-specific content here.</p>
      </div>
    </div>
  `,
  styles: [`
    .hub-container {
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      margin: 10px;
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .hub-content {
      margin-top: 15px;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 4px;
    }
    
    h2 {
      color: #333;
      margin-bottom: 10px;
    }
  `]
})
export class HubComponent {
  title = 'hub-micro-frontend';
} 