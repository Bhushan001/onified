export interface Header {
  key: string;
  value: string;
}

export interface RequestBody {
  [key: string]: any;
}

export interface ApiConfiguration {
  api_name: string;
  baseURL: string;
  headers: Header[];
  request_body: RequestBody;
  // Additional fields for canvas integration
  icon?: string;
  color?: string;
  description?: string;
  category?: string;
  supportedMethods?: string[];
  authTypes?: string[];
}

export interface Configuration {
  configuration: ApiConfiguration[];
}