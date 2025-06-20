import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { ConfigurationService } from './services/configuration.service';
import { CanvasService } from './services/canvas.service';
import { Configuration } from './models/configuration.interface';
import { CanvasNode } from './models/node.interface';
import { ApiSidebarComponent } from './components/api-sidebar/api-sidebar.component';
import { CanvasComponent } from './components/canvas/canvas.component';
import { NodeEditorComponent } from './components/node-editor/node-editor.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ApiSidebarComponent, CanvasComponent, NodeEditorComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'API Integration Canvas';
  configuration: Configuration | null = null;
  isLoading = true;
  error: string | null = null;
  
  // Node editor state
  showNodeEditor = false;
  editingNode: CanvasNode | null = null;
  
  private subscription: Subscription = new Subscription();

  constructor(
    private configService: ConfigurationService,
    private canvasService: CanvasService
  ) {}

  ngOnInit(): void {
    // Subscribe to configuration changes
    this.subscription.add(
      this.configService.getConfiguration().subscribe({
        next: (config) => {
          this.configuration = config;
          this.isLoading = false;
          if (config) {
            console.log('Configuration loaded successfully:', config);
          }
        },
        error: (err) => {
          this.error = 'Failed to load configuration';
          this.isLoading = false;
          console.error('Error loading configuration:', err);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  onClearCanvas(): void {
    this.canvasService.clearCanvas();
  }

  onExportWorkflow(): void {
    const canvasState = this.canvasService.getCurrentState();
    const exportData = {
      nodes: canvasState.nodes,
      connections: canvasState.connections,
      timestamp: new Date().toISOString()
    };
    
    const dataStr = JSON.stringify(exportData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    
    const link = document.createElement('a');
    link.href = URL.createObjectURL(dataBlob);
    link.download = 'workflow.json';
    link.click();
  }

  reloadConfiguration(): void {
    this.isLoading = true;
    this.error = null;
    this.configService.reloadConfiguration();
  }

  onNodeDoubleClick(nodeId: string): void {
    const node = this.canvasService.getNode(nodeId);
    if (node) {
      console.log('Opening editor for node:', node);
      this.editingNode = node;
      this.showNodeEditor = true;
    }
  }

  onCloseNodeEditor(): void {
    this.showNodeEditor = false;
    this.editingNode = null;
  }

  onSaveNodeConfiguration(updatedNode: CanvasNode): void {
    console.log('Saving node configuration:', updatedNode);
    
    // Ensure the name is a string
    if (typeof updatedNode.name !== 'string') {
      console.error('Updated node name is not a string:', updatedNode.name);
      updatedNode.name = updatedNode.apiName?.replace(/_/g, ' ') || 'Unknown Node';
    }
    
    this.canvasService.updateNode(updatedNode.id, updatedNode);
    this.onCloseNodeEditor();
  }
}