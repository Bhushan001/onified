import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-workspace',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="workspace-container">
      <h2>Workspace Application</h2>
      <p>This is the workspace application loaded as a micro-frontend.</p>
      <div class="workspace-content">
        <p>Workspace is successfully loaded through the shell application!</p>
        <p>You can now add your workspace-specific content here.</p>
      </div>
    </div>
  `,
  styles: [`
    .workspace-container {
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      margin: 10px;
      background: white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .workspace-content {
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
export class WorkspaceComponent {
  title = 'workspace-micro-frontend';
} 