import { Component, OnInit, OnDestroy, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { CanvasService } from '../../services/canvas.service';
import { DragDropService } from '../../services/drag-drop.service';
import { CanvasState } from '../../models/canvas.interface';
import { CanvasNode, NodeConnection } from '../../models/node.interface';
import { ApiConfiguration } from '../../models/configuration.interface';
import { NodeComponent } from '../node/node.component';
import { ContextMenuComponent, ContextMenuItem } from '../context-menu/context-menu.component';
import { RenameDialogComponent } from '../rename-dialog/rename-dialog.component';

@Component({
  selector: 'app-canvas',
  standalone: true,
  imports: [CommonModule, NodeComponent, ContextMenuComponent, RenameDialogComponent],
  templateUrl: './canvas.component.html',
  styleUrl: './canvas.component.scss'
})
export class CanvasComponent implements OnInit, OnDestroy {
  @ViewChild('canvasContainer', { static: true }) canvasContainer!: ElementRef<HTMLDivElement>;
  @Output() nodeDoubleClick = new EventEmitter<string>();

  canvasState: CanvasState = {
    nodes: [],
    connections: [],
    selectedNodeId: null,
    canvasOffset: { x: 0, y: 0 },
    zoom: 1
  };

  private subscription = new Subscription();
  private isDraggingCanvas = false;
  private lastMousePosition = { x: 0, y: 0 };
  isDragOver = false;

  // Node dragging state
  private draggingNode: CanvasNode | null = null;
  private nodeDragOffset = { x: 0, y: 0 };

  // Context menu
  contextMenuVisible = false;
  contextMenuPosition = { x: 0, y: 0 };
  contextMenuItems: ContextMenuItem[] = [];
  selectedNodeForContext: CanvasNode | null = null;

  // Rename dialog
  showRenameDialog = false;
  nodeToRename: CanvasNode | null = null;

  constructor(
    public canvasService: CanvasService,
    private dragDropService: DragDropService
  ) {}

  ngOnInit(): void {
    this.subscription.add(
      this.canvasService.canvasState$.subscribe(state => {
        this.canvasState = state;
      })
    );

    // Add global mouse event listeners
    document.addEventListener('mousemove', this.onGlobalMouseMove.bind(this));
    document.addEventListener('mouseup', this.onGlobalMouseUp.bind(this));
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    document.removeEventListener('mousemove', this.onGlobalMouseMove.bind(this));
    document.removeEventListener('mouseup', this.onGlobalMouseUp.bind(this));
  }

  onGlobalMouseMove(event: MouseEvent): void {
    if (this.isDraggingCanvas) {
      const deltaX = event.clientX - this.lastMousePosition.x;
      const deltaY = event.clientY - this.lastMousePosition.y;

      const newOffset = {
        x: this.canvasState.canvasOffset.x + deltaX,
        y: this.canvasState.canvasOffset.y + deltaY
      };

      this.canvasService.updateCanvasOffset(newOffset);
      this.lastMousePosition = { x: event.clientX, y: event.clientY };
    }

    if (this.draggingNode) {
      const newPosition = {
        x: event.clientX - this.nodeDragOffset.x,
        y: event.clientY - this.nodeDragOffset.y
      };

      this.canvasService.moveNode(this.draggingNode.id, newPosition);
    }
  }

  onGlobalMouseUp(): void {
    this.isDraggingCanvas = false;
    this.draggingNode = null;
  }

  onCanvasClick(event: MouseEvent): void {
    if (event.target === this.canvasContainer.nativeElement) {
      this.canvasService.selectNode(null);
    }
  }

  onCanvasMouseDown(event: MouseEvent): void {
    if (event.button === 0 && 
        (event.target === this.canvasContainer.nativeElement || 
         (event.target as HTMLElement).classList.contains('canvas-background') ||
         (event.target as HTMLElement).classList.contains('grid-dots'))) {
      this.isDraggingCanvas = true;
      this.lastMousePosition = { x: event.clientX, y: event.clientY };
      event.preventDefault();
    }
  }

  onCanvasDragEnter(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = true;
  }

  onCanvasDragLeave(event: DragEvent): void {
    event.preventDefault();
    if (!this.canvasContainer.nativeElement.contains(event.relatedTarget as Node)) {
      this.isDragOver = false;
    }
  }

  onCanvasDragOver(event: DragEvent): void {
    event.preventDefault();
    event.dataTransfer!.dropEffect = 'copy';
    this.isDragOver = true;
  }

  onCanvasDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragOver = false;
    
    try {
      const apiConfigData = event.dataTransfer?.getData('application/json');
      if (apiConfigData) {
        const apiConfig: ApiConfiguration = JSON.parse(apiConfigData);
        
        const rect = this.canvasContainer.nativeElement.getBoundingClientRect();
        const position = {
          x: event.clientX - rect.left - 100,
          y: event.clientY - rect.top - 40
        };

        this.canvasService.addNode(apiConfig, position);
      }
    } catch (error) {
      console.error('Error dropping node:', error);
    }

    this.dragDropService.endDrag();
  }

  onNodeSelect(nodeId: string): void {
    this.canvasService.selectNode(nodeId);
  }

  onNodeDoubleClickHandler(nodeId: string): void {
    console.log('Canvas received double click for node:', nodeId);
    this.nodeDoubleClick.emit(nodeId);
  }

  onNodeMouseDown(nodeId: string, event: MouseEvent): void {
    const node = this.canvasState.nodes.find(n => n.id === nodeId);
    if (!node) return;

    this.draggingNode = node;
    this.nodeDragOffset = {
      x: event.clientX - node.position.x,
      y: event.clientY - node.position.y
    };

    this.canvasService.selectNode(nodeId);
    event.stopPropagation();
    event.preventDefault();
  }

  onNodeDelete(nodeId: string): void {
    this.canvasService.deleteNode(nodeId);
  }

  onNodeContextMenu(nodeId: string, event: MouseEvent): void {
    const node = this.canvasState.nodes.find(n => n.id === nodeId);
    if (!node) return;

    this.selectedNodeForContext = node;
    this.contextMenuPosition = { x: event.clientX, y: event.clientY };
    
    this.contextMenuItems = [
      {
        id: 'configure',
        label: 'Configure',
        icon: '⚙️',
        action: () => this.configureNode(nodeId)
      },
      {
        id: 'rename',
        label: 'Rename Node',
        icon: '✏️',
        action: () => this.renameNode(nodeId)
      },
      {
        id: 'separator1',
        label: '',
        icon: '',
        separator: true,
        action: () => {}
      },
      {
        id: 'delete',
        label: 'Delete',
        icon: '🗑️',
        action: () => this.deleteNode(nodeId)
      }
    ];

    this.contextMenuVisible = true;
    this.canvasService.selectNode(nodeId);
  }

  configureNode(nodeId: string): void {
    this.onNodeDoubleClickHandler(nodeId);
  }

  renameNode(nodeId: string): void {
    const node = this.canvasState.nodes.find(n => n.id === nodeId);
    if (node) {
      this.nodeToRename = node;
      this.showRenameDialog = true;
    }
  }

  deleteNode(nodeId: string): void {
    this.canvasService.deleteNode(nodeId);
  }

  onContextMenuClose(): void {
    this.contextMenuVisible = false;
    this.selectedNodeForContext = null;
  }

  onRenameDialogClose(): void {
    this.showRenameDialog = false;
    this.nodeToRename = null;
  }

  onRenameDialogSave(newName: string): void {
    if (this.nodeToRename) {
      const updatedNode = { ...this.nodeToRename, name: newName };
      this.canvasService.updateNode(this.nodeToRename.id, updatedNode);
    }
    this.onRenameDialogClose();
  }

  onZoom(event: WheelEvent): void {
    event.preventDefault();
    
    const zoomFactor = event.deltaY > 0 ? 0.9 : 1.1;
    const newZoom = this.canvasState.zoom * zoomFactor;
    
    this.canvasService.updateZoom(newZoom);
  }

  onZoomIn(): void {
    const newZoom = Math.min(this.canvasState.zoom + 0.1, 3);
    this.canvasService.updateZoom(newZoom);
  }

  onZoomOut(): void {
    const newZoom = Math.max(this.canvasState.zoom - 0.1, 0.1);
    this.canvasService.updateZoom(newZoom);
  }

  onResetZoom(): void {
    this.canvasService.updateZoom(1);
    this.canvasService.updateCanvasOffset({ x: 0, y: 0 });
  }

  getCanvasTransform(): string {
    return `translate(${this.canvasState.canvasOffset.x}px, ${this.canvasState.canvasOffset.y}px) scale(${this.canvasState.zoom})`;
  }

  getConnectionPath(connection: NodeConnection): string {
    const sourceNode = this.canvasState.nodes.find(n => n.id === connection.sourceNodeId);
    const targetNode = this.canvasState.nodes.find(n => n.id === connection.targetNodeId);
    
    if (!sourceNode || !targetNode) return '';
    
    const startX = sourceNode.position.x + sourceNode.size.width;
    const startY = sourceNode.position.y + sourceNode.size.height / 2;
    const endX = targetNode.position.x;
    const endY = targetNode.position.y + targetNode.size.height / 2;
    
    return `M ${startX} ${startY} L ${endX} ${endY}`;
  }
}