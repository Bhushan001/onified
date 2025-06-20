import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CanvasNode } from '../../models/node.interface';

@Component({
  selector: 'app-node',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './node.component.html',
  styleUrl: './node.component.scss'
})
export class NodeComponent {
  @Input() node!: CanvasNode;
  @Output() nodeSelect = new EventEmitter<string>();
  @Output() nodeMouseDown = new EventEmitter<{ nodeId: string, event: MouseEvent }>();
  @Output() nodeDelete = new EventEmitter<string>();
  @Output() nodeDoubleClick = new EventEmitter<string>();
  @Output() nodeContextMenu = new EventEmitter<{ nodeId: string, event: MouseEvent }>();

  onMouseDown(event: MouseEvent): void {
    if (event.button !== 0) return;
    
    const target = event.target as HTMLElement;
    if (target.closest('.delete-btn') || target.closest('.connection-point')) {
      return;
    }

    this.nodeMouseDown.emit({ nodeId: this.node.id, event });
  }

  onSingleClick(event: MouseEvent): void {
    event.stopPropagation();
    
    const target = event.target as HTMLElement;
    if (target.closest('.delete-btn') || target.closest('.connection-point')) {
      return;
    }

    this.nodeSelect.emit(this.node.id);
  }

  onDoubleClick(event: MouseEvent): void {
    event.stopPropagation();
    event.preventDefault();
    
    const target = event.target as HTMLElement;
    if (target.closest('.delete-btn') || target.closest('.connection-point')) {
      return;
    }

    this.nodeDoubleClick.emit(this.node.id);
  }

  onContextMenu(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
    
    const target = event.target as HTMLElement;
    if (target.closest('.delete-btn') || target.closest('.connection-point')) {
      return;
    }

    this.nodeContextMenu.emit({ nodeId: this.node.id, event });
  }

  onDeleteClick(event: MouseEvent): void {
    event.stopPropagation();
    event.preventDefault();
    this.nodeDelete.emit(this.node.id);
  }

  getNodeStyle(): any {
    return {
      position: 'absolute',
      left: this.node.position.x + 'px',
      top: this.node.position.y + 'px',
      width: this.node.size.width + 'px',
      minHeight: this.node.size.height + 'px'
    };
  }

  isTriggerNode(): boolean {
    return this.node.apiName === 'manual_trigger';
  }

  getDisplayName(): string {
    // Ensure we always return a string
    if (this.node && this.node.name && typeof this.node.name === 'string') {
      return this.node.name;
    }
    
    // Fallback to apiName if name is not a proper string
    if (this.node && this.node.apiName && typeof this.node.apiName === 'string') {
      return this.node.apiName.replace(/_/g, ' ');
    }
    
    // Final fallback
    return 'Unknown Node';
  }

  getApiIcon(): string {
    const iconMap: { [key: string]: string } = {
      'manual_trigger': '▶️',
      'jsonplaceholder': '📝',
      'httpbin': '🔧',
      'catfacts': '🐱',
      'reqres': '👤',
      'openai': '🤖'
    };
    return iconMap[this.node.apiName] || '🔗';
  }

  getApiColor(): string {
    const colorMap: { [key: string]: string } = {
      'manual_trigger': '#FF6B35',
      'jsonplaceholder': '#4CAF50',
      'httpbin': '#2196F3',
      'catfacts': '#FF9800',
      'reqres': '#9C27B0',
      'openai': '#10A37F'
    };
    return colorMap[this.node.apiName] || '#607D8B';
  }
}