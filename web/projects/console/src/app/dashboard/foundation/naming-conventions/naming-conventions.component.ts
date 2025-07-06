import { Component } from '@angular/core';

@Component({
  selector: 'app-naming-conventions',
  standalone: true,
  template: `
    <div class="naming-conventions-section">
      <h2>Console Naming Conventions</h2>
      <p>Naming conventions settings go here.</p>
    </div>
  `,
  styles: [`
    .naming-conventions-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class NamingConventionsComponent {} 