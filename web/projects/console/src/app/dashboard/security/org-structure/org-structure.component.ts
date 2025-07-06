import { Component } from '@angular/core';

@Component({
  selector: 'app-org-structure',
  standalone: true,
  template: `
    <div class="org-structure-section">
      <h2>Console Organization Structure</h2>
      <p>Organization structure management goes here.</p>
    </div>
  `,
  styles: [`
    .org-structure-section {
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    }
  `]
})
export class OrgStructureComponent {} 