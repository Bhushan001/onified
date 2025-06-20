export interface NodePosition {
  x: number;
  y: number;
}

export interface NodeSize {
  width: number;
  height: number;
}

export interface NodeConnection {
  id: string;
  sourceNodeId: string;
  targetNodeId: string;
  sourcePort: string;
  targetPort: string;
}

export interface NodePort {
  id: string;
  type: 'input' | 'output';
  label: string;
  dataType: string;
}

export interface NodeData {
  apiConfiguration: any;
  authConfig?: {
    type: 'none' | 'basic' | 'bearer' | 'apikey' | 'oauth';
    credentials?: any;
  };
  parameters?: { [key: string]: any };
  customHeaders?: { [key: string]: string };
  requestBody?: any;
}

export interface CanvasNode {
  id: string;
  type: 'api' | 'trigger' | 'action';
  name: string;
  apiName: string;
  position: NodePosition;
  size: NodeSize;
  data: NodeData;
  ports: NodePort[];
  isSelected: boolean;
  isConfigured: boolean;
}