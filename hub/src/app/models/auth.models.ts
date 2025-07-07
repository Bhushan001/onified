/**
 * Authentication Models for Onified.ai Application
 * 
 * This file contains all TypeScript interfaces and types related to authentication.
 * These models ensure type safety and provide clear contracts for API communication.
 * 
 * Updated to handle the specific response format from your backend.
 */

/**
 * Base login request interface
 * Contains common fields for all login types
 */
interface BaseLoginRequest {
  /** Password - optional for phone-based authentication (may use OTP instead) */
  password?: string;
}

/**
 * Username-based login request
 * Used when user logs in with username
 */
export interface UsernameLoginRequest extends BaseLoginRequest {
  /** Username for login */
  username: string;
}

/**
 * Phone-based login request
 * Used when user logs in with phone number
 */
export interface PhoneLoginRequest extends BaseLoginRequest {
  /** Phone number for login */
  phone: string;
  /** OTP code for phone authentication */
  otp?: string;
}

/**
 * Domain-based login request
 * Used when user logs in with tenant domain
 */
export interface DomainLoginRequest extends BaseLoginRequest {
  /** Tenant domain for login */
  domain: string;
}

/**
 * Union type for all possible login request formats
 * The actual request sent to the backend will use one of these specific formats
 */
export type LoginRequest = UsernameLoginRequest | PhoneLoginRequest | DomainLoginRequest;

/**
 * Internal login request interface used by the frontend
 * This is what the component uses internally before converting to specific format
 */
export interface InternalLoginRequest {
  /** User identifier - can be username, phone number, or tenant domain */
  identifier: string;
  /** Password - optional for phone-based authentication (may use OTP instead) */
  password?: string;
  /** Detected identifier type */
  identifierType?: 'username' | 'phone' | 'domain';
}

/**
 * Response from login API endpoint
 * Updated to match your backend's response format
 */
export interface LoginResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Response body containing authentication data */
  body?: {
    /** JWT access token (Keycloak returns as accessToken) */
    accessToken?: string;
    /** JWT access token (legacy/compat) */
    jwtToken?: string;
    /** Username of the authenticated user */
    username: string;
    /** Optional refresh token */
    refreshToken?: string;
    /** Token expiration time in seconds */
    expiresIn?: number;
    /** Additional user data */
    user?: User;
    /** User profile details (roles, etc.) */
    userProfile?: UserAuthDetailsResponse;
  };
  /** Error message if login failed */
  message?: string;
  /** Additional error details */
  error?: string;
}

/**
 * User profile and account information
 * Contains all user-related data returned from authentication
 * Updated to use username as primary identifier instead of email
 */
export interface User {
  /** Unique user identifier */
  id: string;
  /** Username for login and identification */
  username: string;
  /** User's email address (optional, for notifications) */
  email?: string;
  /** User's display name */
  name: string;
  /** User's first name (optional) */
  firstName?: string;
  /** User's last name (optional) */
  lastName?: string;
  /** Tenant/organization identifier (for multi-tenant applications) */
  tenant?: string;
  /** User's roles and permissions */
  roles?: string[];
  /** URL to user's avatar image */
  avatar?: string;
  /** Timestamp of last login */
  lastLogin?: string;
  /** User's phone number (optional) */
  phone?: string;
  /** User's department or team (optional) */
  department?: string;
  /** User account status */
  status?: 'active' | 'inactive' | 'suspended';
}

/**
 * Request payload for QR code authentication
 * Used for passwordless login via QR code scanning
 */
export interface QRLoginRequest {
  /** QR code data/token */
  qrCode?: string;
  /** Unique device identifier for security */
  deviceId?: string;
}

/**
 * Response from QR login API endpoint
 * Updated to match your backend's response format
 */
export interface QRLoginResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Response body containing authentication data */
  body?: {
    /** JWT access token */
    accessToken: string;
    /** JWT access token */
    jwtToken: string;
    /** Username of the authenticated user */
    username: string;
    /** Optional refresh token */
    refreshToken?: string;
    /** Token expiration time in seconds */
    expiresIn?: number;
    /** Additional user data */
    user?: User;
  };
  /** Error message if QR login failed */
  message?: string;
  /** Additional error details */
  error?: string;
}

/**
 * Request payload for token refresh
 * Used to obtain new access token using refresh token
 */
export interface RefreshTokenRequest {
  /** Refresh token obtained during initial authentication */
  refreshToken: string;
}

/**
 * Response from token refresh API endpoint
 * Updated to match your backend's response format
 */
export interface RefreshTokenResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Response body containing new tokens */
  body?: {
    /** New JWT access token (Keycloak returns as accessToken) */
    accessToken?: string;
    /** New JWT access token (legacy/compat) */
    jwtToken?: string;
    /** New refresh token (if rotation is enabled) */
    refreshToken?: string;
    /** New token expiration time in seconds */
    expiresIn?: number;
  };
  /** Error message if token refresh failed */
  message?: string;
  /** Additional error details */
  error?: string;
}

/**
 * Response from logout API endpoint
 * Updated to match your backend's response format
 */
export interface LogoutResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Human-readable message about the logout */
  message?: string;
}

/**
 * User registration request payload
 * For creating new user accounts with username-based authentication
 */
export interface RegisterRequest {
  /** Desired username for the account */
  username: string;
  /** User's email address */
  email: string;
  /** Account password */
  password: string;
  /** Password confirmation */
  confirmPassword: string;
  /** User's full name */
  name: string;
  /** User's first name (optional) */
  firstName?: string;
  /** User's last name (optional) */
  lastName?: string;
  /** User's phone number (optional) */
  phone?: string;
  /** Tenant/organization identifier (optional) */
  tenant?: string;
}

/**
 * User registration response
 * Updated to match your backend's response format
 */
export interface RegisterResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Response body containing user data */
  body?: {
    /** Created user information */
    user: User;
    /** Optional immediate login token */
    jwtToken?: string;
  };
  /** Error message if registration failed */
  message?: string;
  /** Additional error details */
  error?: string;
}

/**
 * Password reset request payload
 * For initiating password reset flow
 */
export interface PasswordResetRequest {
  /** Username or email for password reset */
  identifier: string;
}

/**
 * Password reset response
 * Updated to match your backend's response format
 */
export interface PasswordResetResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Human-readable message about the password reset */
  message?: string;
  /** Additional error details */
  error?: string;
}

/**
 * User profile update request
 * For updating user profile information
 */
export interface UpdateProfileRequest {
  /** User's display name */
  name?: string;
  /** User's first name */
  firstName?: string;
  /** User's last name */
  lastName?: string;
  /** User's email address */
  email?: string;
  /** User's phone number */
  phone?: string;
  /** User's department or team */
  department?: string;
  /** URL to user's avatar image */
  avatar?: string;
}

/**
 * User profile update response
 * Updated to match your backend's response format
 */
export interface UpdateProfileResponse {
  /** HTTP status code */
  statusCode: number;
  /** Status message (SUCCESS, ERROR, etc.) */
  status: string;
  /** Response body containing updated user data */
  body?: {
    /** Updated user information */
    user: User;
  };
  /** Error message if update failed */
  message?: string;
  /** Additional error details */
  error?: string;
}

export interface UserAuthDetailsResponse {
  id: string;
  username: string;
  passwordHash: string;
  roles: string[];
}