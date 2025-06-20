import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CanvasState } from '../models/canvas.interface';
import { CanvasNode, NodeConnection, NodePosition } from '../models/node.interface';
import { ApiConfiguration } from '../models/configuration.interface';

@Injectable({
  providedIn: 'root'
})
export class CanvasService {
  private canvasStateSubject = new BehaviorSubject<CanvasState>({
    nodes: [],
    connections: [],
    selectedNodeId: null,
    canvasOffset: { x: 0, y: 0 },
    zoom: 1
  });

  public canvasState$ = this.canvasStateSubject.asObservable();

  constructor() {}

  // Node Management
  addNode(apiConfig: ApiConfiguration, position: NodePosition): string {
    const nodeId = this.generateNodeId();
    const isTrigger = apiConfig.api_name === 'manual_trigger';
    
    // Ensure we create a proper string name
    let nodeName: string;
    if (typeof apiConfig.api_name === 'string') {
      nodeName = apiConfig.api_name.replace(/_/g, ' ');
    } else {
      nodeName = 'Unknown Node';
    }
    
    const newNode: CanvasNode = {
      id: nodeId,
      type: isTrigger ? 'trigger' : 'api',
      name: nodeName,
      apiName: apiConfig.api_name,
      position,
      size: { width: 220, height: 90 },
      data: {
        apiConfiguration: apiConfig,
        authConfig: { type: 'none' },
        parameters: {},
        customHeaders: {},
        requestBody: apiConfig.request_body || {}
      },
      ports: isTrigger ? 
        [{ id: `${nodeId}-output`, type: 'output', label: 'Output', dataType: 'any' }] :
        [
          { id: `${nodeId}-input`, type: 'input', label: 'Input', dataType: 'any' },
          { id: `${nodeId}-output`, type: 'output', label: 'Output', dataType: 'any' }
        ],
      isSelected: false,
      isConfigured: isTrigger
    };

    const currentState = this.canvasStateSubject.value;
    this.canvasStateSubject.next({
      ...currentState,
      nodes: [...currentState.nodes, newNode]
    });

    return nodeId;
  }

  updateNode(nodeId: string, updates: Partial<CanvasNode>): void {
    const currentState = this.canvasStateSubject.value;
    const nodeIndex = currentState.nodes.findIndex(node => node.id === nodeId);
    
    if (nodeIndex !== -1) {
      const updatedNodes = [...currentState.nodes];
      const originalNode = updatedNodes[nodeIndex];
      
      // Ensure name stays as string during updates
      const updatedNode = { ...originalNode, ...updates };
      if (typeof updatedNode.name !== 'string') {
        updatedNode.name = String(originalNode.apiName || 'Unknown Node').replace(/_/g, ' ');
      }
      
      updatedNodes[nodeIndex] = updatedNode;
      
      this.canvasStateSubject.next({
        ...currentState,
        nodes: updatedNodes
      });
    }
  }

  deleteNode(nodeId: string): void {
    const currentState = this.canvasStateSubject.value;
    const filteredNodes = currentState.nodes.filter(node => node.id !== nodeId);
    const filteredConnections = currentState.connections.filter(
      conn => conn.sourceNodeId !== nodeId && conn.targetNodeId !== nodeId
    );

    this.canvasStateSubject.next({
      ...currentState,
      nodes: filteredNodes,
      connections: filteredConnections,
      selectedNodeId: currentState.selectedNodeId === nodeId ? null : currentState.selectedNodeId
    });
  }

  selectNode(nodeId: string | null): void {
    const currentState = this.canvasStateSubject.value;
    const updatedNodes = currentState.nodes.map(node => ({
      ...node,
      isSelected: node.id === nodeId
    }));

    this.canvasStateSubject.next({
      ...currentState,
      nodes: updatedNodes,
      selectedNodeId: nodeId
    });
  }

  moveNode(nodeId: string, newPosition: NodePosition): void {
    this.updateNode(nodeId, { position: newPosition });
  }

  // Connection Management
  addConnection(connection: Omit<NodeConnection, 'id'>): void {
    const connectionId = this.generateConnectionId();
    const newConnection: NodeConnection = {
      id: connectionId,
      ...connection
    };

    const currentState = this.canvasStateSubject.value;
    this.canvasStateSubject.next({
      ...currentState,
      connections: [...currentState.connections, newConnection]
    });
  }

  deleteConnection(connectionId: string): void {
    const currentState = this.canvasStateSubject.value;
    const filteredConnections = currentState.connections.filter(conn => conn.id !== connectionId);

    this.canvasStateSubject.next({
      ...currentState,
      connections: filteredConnections
    });
  }

  // Canvas Management
  updateCanvasOffset(offset: { x: number; y: number }): void {
    const currentState = this.canvasStateSubject.value;
    this.canvasStateSubject.next({
      ...currentState,
      canvasOffset: offset
    });
  }

  updateZoom(zoom: number): void {
    const currentState = this.canvasStateSubject.value;
    this.canvasStateSubject.next({
      ...currentState,
      zoom: Math.max(0.1, Math.min(3, zoom))
    });
  }

  // Utility Methods
  getNode(nodeId: string): CanvasNode | null {
    const currentState = this.canvasStateSubject.value;
    return currentState.nodes.find(node => node.id === nodeId) || null;
  }

  getSelectedNode(): CanvasNode | null {
    const currentState = this.canvasStateSubject.value;
    return currentState.selectedNodeId ? this.getNode(currentState.selectedNodeId) : null;
  }

  clearCanvas(): void {
    this.canvasStateSubject.next({
      nodes: [],
      connections: [],
      selectedNodeId: null,
      canvasOffset: { x: 0, y: 0 },
      zoom: 1
    });
  }

  private generateNodeId(): string {
    return 'node_' + Math.random().toString(36).substr(2, 9);
  }

  private generateConnectionId(): string {
    return 'conn_' + Math.random().toString(36).substr(2, 9);
  }

  getCurrentState(): CanvasState {
    return this.canvasStateSubject.value;
  }
}