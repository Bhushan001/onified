// Authentication Models for Onified.ai Application

export interface UsernameLoginRequest { username: string; password?: string; }
export interface PhoneLoginRequest { phone: string; password?: string; otp?: string; }
export interface DomainLoginRequest { domain: string; password?: string; }
export type LoginRequest = UsernameLoginRequest | PhoneLoginRequest | DomainLoginRequest;
export interface InternalLoginRequest { identifier: string; password?: string; identifierType?: 'username' | 'phone' | 'domain'; }
export interface LoginResponse {
  statusCode: number;
  status: string;
  body?: {
    accessToken?: string;
    jwtToken?: string;
    username: string;
    refreshToken?: string;
    expiresIn?: number;
    user?: User;
    userProfile?: UserAuthDetailsResponse;
  };
  message?: string;
  error?: string;
}
export interface User {
  id: string;
  username: string;
  email?: string;
  name: string;
  firstName?: string;
  lastName?: string;
  tenant?: string;
  roles?: string[];
  avatar?: string;
  lastLogin?: string;
  phone?: string;
  department?: string;
  status?: 'active' | 'inactive' | 'suspended';
}
export interface QRLoginRequest { qrCode?: string; deviceId?: string; }
export interface QRLoginResponse {
  statusCode: number;
  status: string;
  body?: {
    accessToken: string;
    jwtToken: string;
    username: string;
    refreshToken?: string;
    expiresIn?: number;
    user?: User;
  };
  message?: string;
  error?: string;
}
export interface RefreshTokenResponse {
  statusCode: number;
  status: string;
  body?: {
    accessToken?: string;
    jwtToken?: string;
    refreshToken?: string;
    expiresIn?: number;
  };
  message?: string;
  error?: string;
}
export interface LogoutResponse {
  statusCode: number;
  status: string;
  message?: string;
}
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  name: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  tenant?: string;
}
export interface UpdateProfileRequest {
  name?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  phone?: string;
  department?: string;
  avatar?: string;
}
export interface UpdateProfileResponse {
  statusCode: number;
  status: string;
  body?: { user: User };
  message?: string;
  error?: string;
}
export interface UserAuthDetailsResponse {
  id: string;
  username: string;
  passwordHash: string;
  roles: string[];
} 