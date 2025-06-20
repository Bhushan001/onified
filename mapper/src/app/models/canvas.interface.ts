import { CanvasNode, NodeConnection } from './node.interface';

export interface CanvasState {
  nodes: CanvasNode[];
  connections: NodeConnection[];
  selectedNodeId: string | null;
  canvasOffset: { x: number; y: number };
  zoom: number;
}

export interface DragState {
  isDragging: boolean;
  dragType: 'node' | 'canvas' | 'connection';
  dragData: any;
  startPosition: { x: number; y: number };
}