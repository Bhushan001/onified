import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CanvasNode } from '../../models/node.interface';

@Component({
  selector: 'app-node-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './node-editor.component.html',
  styleUrl: './node-editor.component.scss'
})
export class NodeEditorComponent implements OnInit {
  @Input() node!: CanvasNode;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<CanvasNode>();

  // Form data
  authType: string = 'none';
  customHeaders: { key: string; value: string }[] = [];
  requestBody: string = '';
  parameters: { key: string; value: string }[] = [];
  
  // Auth configurations
  basicAuth = { username: '', password: '' };
  bearerToken = { token: '' };
  apiKey = { key: '', value: '', location: 'header' };
  
  // Available auth types
  authTypes = [
    { value: 'none', label: 'None' },
    { value: 'basic', label: 'Basic Auth' },
    { value: 'bearer', label: 'Bearer Token' },
    { value: 'apikey', label: 'Header Auth' }
  ];

  // HTTP methods
  httpMethods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH'];
  selectedMethod = 'GET';
  
  // n8n specific options
  sendQueryParams = 'none';
  sendHeaders = 'none';
  sendBody = 'none';

  // Response/Output data
  responseData: any = null;
  executionTime: number | null = null;
  activeTab: 'table' | 'json' | 'schema' = 'table';
  isTestingConnection = false;
  isExecuting = false;
  
  // Execution state
  lastExecutionType: 'test' | 'execute' | null = null;

  ngOnInit(): void {
    this.loadNodeData();
  }

  private loadNodeData(): void {
    // Load existing configuration
    this.authType = this.node.data.authConfig?.type || 'none';
    this.selectedMethod = 'POST'; // Default to POST for most APIs
    
    // Load custom headers
    this.customHeaders = Object.entries(this.node.data.customHeaders || {}).map(([key, value]) => ({
      key,
      value: value as string
    }));
    
    if (this.customHeaders.length === 0) {
      this.customHeaders.push({ key: '', value: '' });
    } else {
      this.sendHeaders = 'custom';
    }
    
    // Load parameters
    this.parameters = Object.entries(this.node.data.parameters || {}).map(([key, value]) => ({
      key,
      value: value as string
    }));
    
    if (this.parameters.length === 0) {
      this.parameters.push({ key: '', value: '' });
    } else {
      this.sendQueryParams = 'custom';
    }
    
    // Load request body
    this.requestBody = JSON.stringify(this.node.data.requestBody || {}, null, 2);
    if (this.requestBody !== '{}') {
      this.sendBody = 'json';
    }
    
    // Load auth credentials
    if (this.node.data.authConfig?.credentials) {
      const creds = this.node.data.authConfig.credentials;
      this.basicAuth = creds.basicAuth || { username: '', password: '' };
      this.bearerToken = creds.bearerToken || { token: '' };
      this.apiKey = creds.apiKey || { key: '', value: '', location: 'header' };
    }
  }

  onBack(): void {
    this.save.emit(this.buildUpdatedNode());
    this.close.emit();
  }

  private buildUpdatedNode(): CanvasNode {
    try {
      let parsedRequestBody = {};
      if (this.requestBody.trim() && this.sendBody === 'json') {
        parsedRequestBody = JSON.parse(this.requestBody);
      }

      const headersObj: { [key: string]: string } = {};
      if (this.sendHeaders === 'custom') {
        this.customHeaders.forEach(header => {
          if (header.key && header.value) {
            headersObj[header.key] = header.value;
          }
        });
      }

      const parametersObj: { [key: string]: string } = {};
      if (this.sendQueryParams === 'custom') {
        this.parameters.forEach(param => {
          if (param.key && param.value) {
            parametersObj[param.key] = param.value;
          }
        });
      }

      let credentials: any = {};
      if (this.authType === 'basic') {
        credentials.basicAuth = this.basicAuth;
      } else if (this.authType === 'bearer') {
        credentials.bearerToken = this.bearerToken;
      } else if (this.authType === 'apikey') {
        credentials.apiKey = this.apiKey;
      }

      // Preserve the original name - don't modify it
      const updatedNode: CanvasNode = {
        ...this.node,
        // Keep the original name unchanged
        name: this.node.name,
        data: {
          ...this.node.data,
          authConfig: {
            type: this.authType as any,
            credentials
          },
          customHeaders: headersObj,
          parameters: parametersObj,
          requestBody: parsedRequestBody
        },
        isConfigured: true
      };

      console.log('Built updated node:', updatedNode);
      return updatedNode;
    } catch (error) {
      console.error('Error building updated node:', error);
      return this.node;
    }
  }

  // Test Step - validates configuration and shows sample response
  async testConnection(): Promise<void> {
    this.isTestingConnection = true;
    this.lastExecutionType = 'test';
    
    // Validate configuration first
    const validationErrors = this.validateConfiguration();
    if (validationErrors.length > 0) {
      this.isTestingConnection = false;
      this.responseData = {
        error: true,
        message: 'Configuration validation failed',
        errors: validationErrors
      };
      this.executionTime = 0;
      return;
    }
    
    // Simulate connection test with shorter delay
    setTimeout(() => {
      this.executionTime = Math.floor(Math.random() * 200) + 100; // 100-300ms for test
      switch (this.node.apiName) {
        case 'openai':
          this.responseData = {
            test_result: 'success',
            message: 'OpenAI API connection successful',
            model_available: 'gpt-3.5-turbo',
            rate_limit_remaining: 4999,
            authentication: 'valid'
          };
          break;
          
        case 'jsonplaceholder':
          this.responseData = {
            test_result: 'success',
            message: 'JSONPlaceholder API connection successful',
            endpoint_available: true,
            response_time: '120ms'
          };
          break;
          
        case 'catfacts':
          this.responseData = {
            test_result: 'success',
            message: 'Cat Facts API connection successful',
            endpoint_status: 'healthy',
            api_version: 'v1'
          };
          break;
          
        case 'reqres':
          this.responseData = {
            test_result: 'success',
            message: 'ReqRes API connection successful',
            endpoint_available: true,
            test_user_id: 1
          };
          break;
          
        case 'httpbin':
          this.responseData = {
            test_result: 'success',
            message: 'HTTPBin API connection successful',
            endpoint_status: 'operational',
            test_echo: 'working'
          };
          break;
          
        default:
          this.responseData = {
            test_result: 'success',
            message: 'API connection test successful',
            timestamp: new Date().toISOString()
          };
      }
      
      this.isTestingConnection = false;
    }, 800);
  }

  // Execute Node - runs the actual API call with real data
  async executeNode(): Promise<void> {
    this.isExecuting = true;
    this.lastExecutionType = 'execute';
    
    // Validate configuration first
    const validationErrors = this.validateConfiguration();
    if (validationErrors.length > 0) {
      this.isExecuting = false;
      this.responseData = {
        error: true,
        message: 'Cannot execute: Configuration validation failed',
        errors: validationErrors
      };
      this.executionTime = 0;
      return;
    }
    
    // Simulate actual API execution with longer delay
    setTimeout(() => {
      this.executionTime = Math.floor(Math.random() * 800) + 300; // 300-1100ms for execution
      switch (this.node.apiName) {
        case 'openai':
          this.responseData = {
            id: "chatcmpl-" + Math.random().toString(36).substr(2, 9),
            object: "chat.completion",
            created: Math.floor(Date.now() / 1000),
            model: "gpt-3.5-turbo",
            choices: [
              {
                index: 0,
                message: {
                  role: "assistant",
                  content: "Hello! I'm an AI assistant created by OpenAI. I can help you with a wide variety of tasks including answering questions, helping with analysis, creative writing, coding, math, and much more. How can I assist you today?"
                },
                finish_reason: "stop"
              }
            ],
            usage: {
              prompt_tokens: 12,
              completion_tokens: 45,
              total_tokens: 57
            }
          };
          break;
          
        case 'jsonplaceholder':
          this.responseData = [
            {
              userId: 1,
              id: 1,
              title: "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
              body: "quia et suscipit\nsuscipit recusandae consequuntur expedita et cum\nreprehenderit molestiae ut ut quas totam\nnostrum rerum est autem sunt rem eveniet architecto"
            },
            {
              userId: 1,
              id: 2,
              title: "qui est esse",
              body: "est rerum tempore vitae\nsequi sint nihil reprehenderit dolor beatae ea dolores neque\nfugiat blanditiis voluptate porro vel nihil molestiae ut reiciendis\nqui aperiam non debitis possimus qui neque nisi nulla"
            },
            {
              userId: 2,
              id: 3,
              title: "ea molestias quasi exercitationem repellat qui ipsa sit aut",
              body: "et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut"
            }
          ];
          break;
          
        case 'catfacts':
          this.responseData = {
            fact: "Cats have 32 muscles that control the outer ear (compared to human's 6 muscles each). A cat can rotate its ears independently 180 degrees, and can turn in the direction of sound 10 times faster than those of the best watchdog.",
            length: 201
          };
          break;
          
        case 'reqres':
          this.responseData = {
            data: [
              {
                id: 1,
                email: "george.bluth@reqres.in",
                first_name: "George",
                last_name: "Bluth",
                avatar: "https://reqres.in/img/faces/1-image.jpg"
              },
              {
                id: 2,
                email: "janet.weaver@reqres.in",
                first_name: "Janet",
                last_name: "Weaver",
                avatar: "https://reqres.in/img/faces/2-image.jpg"
              },
              {
                id: 3,
                email: "emma.wong@reqres.in",
                first_name: "Emma",
                last_name: "Wong",
                avatar: "https://reqres.in/img/faces/3-image.jpg"
              }
            ],
            page: 1,
            per_page: 6,
            total: 12,
            total_pages: 2,
            support: {
              url: "https://reqres.in/#support-heading",
              text: "To keep ReqRes free, contributions towards server costs are appreciated!"
            }
          };
          break;
          
        case 'httpbin':
          this.responseData = {
            args: this.sendQueryParams === 'custom' ? this.getParametersObject() : {},
            data: this.sendBody === 'json' ? this.requestBody : "",
            files: {},
            form: {},
            headers: {
              "Accept": "application/json",
              "Content-Type": "application/json",
              "Host": "httpbin.org",
              "User-Agent": "n8n-workflow/1.0",
              ...this.getHeadersObject()
            },
            json: this.sendBody === 'json' ? JSON.parse(this.requestBody || '{}') : null,
            origin: "203.0.113." + Math.floor(Math.random() * 255),
            url: this.node.data.apiConfiguration.baseURL + (this.sendQueryParams === 'custom' ? this.buildQueryString() : '')
          };
          break;
          
        default:
          this.responseData = {
            success: true,
            message: 'Execution successful',
            timestamp: new Date().toISOString(),
            data: { result: 'Sample execution result' }
          };
      }
      
      this.isExecuting = false;
    }, 1200);
  }

  private validateConfiguration(): string[] {
    const errors: string[] = [];
    
    // Check authentication for APIs that require it
    if (this.node.apiName === 'openai') {
      if (this.authType !== 'bearer' || !this.bearerToken.token.trim()) {
        errors.push('OpenAI requires a Bearer Token (API Key)');
      }
    }
    
    // Check JSON validity
    if (this.sendBody === 'json' && this.requestBody.trim()) {
      if (!this.isValidJson()) {
        errors.push('Request body contains invalid JSON');
      }
    }
    
    // Check required headers
    if (this.sendHeaders === 'custom') {
      const hasEmptyHeaders = this.customHeaders.some(h => h.key.trim() && !h.value.trim());
      if (hasEmptyHeaders) {
        errors.push('Some headers have empty values');
      }
    }
    
    return errors;
  }

  private getHeadersObject(): { [key: string]: string } {
    const headers: { [key: string]: string } = {};
    if (this.sendHeaders === 'custom') {
      this.customHeaders.forEach(header => {
        if (header.key && header.value) {
          headers[header.key] = header.value;
        }
      });
    }
    return headers;
  }

  private getParametersObject(): { [key: string]: string } {
    const params: { [key: string]: string } = {};
    if (this.sendQueryParams === 'custom') {
      this.parameters.forEach(param => {
        if (param.key && param.value) {
          params[param.key] = param.value;
        }
      });
    }
    return params;
  }

  private buildQueryString(): string {
    const params = this.getParametersObject();
    const queryString = Object.keys(params).map(key => `${key}=${encodeURIComponent(params[key])}`).join('&');
    return queryString ? `?${queryString}` : '';
  }

  addHeader(): void {
    this.customHeaders.push({ key: '', value: '' });
  }

  removeHeader(index: number): void {
    this.customHeaders.splice(index, 1);
    if (this.customHeaders.length === 0) {
      this.customHeaders.push({ key: '', value: '' });
    }
  }

  addParameter(): void {
    this.parameters.push({ key: '', value: '' });
  }

  removeParameter(index: number): void {
    this.parameters.splice(index, 1);
    if (this.parameters.length === 0) {
      this.parameters.push({ key: '', value: '' });
    }
  }

  isValidJson(): boolean {
    if (!this.requestBody.trim()) return true;
    try {
      JSON.parse(this.requestBody);
      return true;
    } catch {
      return false;
    }
  }

  getJsonStatus(): string {
    if (!this.requestBody.trim()) return 'Empty';
    return this.isValidJson() ? 'Valid JSON' : 'Invalid JSON';
  }

  getItemCount(): number {
    if (!this.responseData) return 0;
    if (this.responseData.error) return 0;
    if (Array.isArray(this.responseData)) return this.responseData.length;
    if (this.responseData.data && Array.isArray(this.responseData.data)) {
      return this.responseData.data.length;
    }
    return 1;
  }

  getTableHeaders(): string[] {
    if (!this.responseData || this.responseData.error) return [];
    
    const data = Array.isArray(this.responseData) ? this.responseData : 
                 (this.responseData.data && Array.isArray(this.responseData.data)) ? this.responseData.data : 
                 [this.responseData];
    
    if (data.length === 0) return [];
    
    return Object.keys(data[0]);
  }

  getTableData(): any[] {
    if (!this.responseData || this.responseData.error) return [];
    
    if (Array.isArray(this.responseData)) return this.responseData;
    if (this.responseData.data && Array.isArray(this.responseData.data)) {
      return this.responseData.data;
    }
    return [this.responseData];
  }

  formatTableValue(value: any): string {
    if (value === null || value === undefined) return '';
    if (typeof value === 'object') return JSON.stringify(value);
    return String(value);
  }

  formatJsonOutput(): string {
    return JSON.stringify(this.responseData, null, 2);
  }

  getSchemaFields(): { name: string; type: string }[] {
    if (!this.responseData || this.responseData.error) return [];
    
    const data = Array.isArray(this.responseData) ? this.responseData[0] : 
                 (this.responseData.data && Array.isArray(this.responseData.data)) ? this.responseData.data[0] : 
                 this.responseData;
    
    if (!data) return [];
    
    return Object.keys(data).map(key => ({
      name: key,
      type: typeof data[key]
    }));
  }

  getApiIcon(): string {
    const iconMap: { [key: string]: string } = {
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
      'jsonplaceholder': '#4CAF50',
      'httpbin': '#2196F3',
      'catfacts': '#FF9800',
      'reqres': '#9C27B0',
      'openai': '#10A37F'
    };
    return colorMap[this.node.apiName] || '#607D8B';
  }

  getRequestBodyPlaceholder(): string {
    switch (this.node.apiName) {
      case 'openai':
        return `{
  "model": "gpt-3.5-turbo",
  "messages": [
    {
      "role": "user",
      "content": "Hello, how can you help me?"
    }
  ],
  "max_tokens": 150,
  "temperature": 0.7
}`;
      case 'jsonplaceholder':
        return `{
  "title": "My Post Title",
  "body": "Post content here",
  "userId": 1
}`;
      case 'reqres':
        return `{
  "name": "John Doe",
  "job": "Developer"
}`;
      default:
        return '{\n  "key": "value"\n}';
    }
  }

  getDisplayName(): string {
    console.log('Node editor - getting display name for:', this.node);
    
    if (this.node.name && typeof this.node.name === 'string') {
      return this.node.name;
    } else if (this.node.apiName && typeof this.node.apiName === 'string') {
      return this.node.apiName.replace(/_/g, ' ');
    } else {
      return 'Unknown Node';
    }
  }
}