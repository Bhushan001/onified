export const environment = {
  production: false,
  apiUrl: 'http://localhost:9080/api',
  appName: 'Onified.ai',
  version: '1.0.0',
  enableLogging: true,
  theme: {
    default: 'light',
    allowToggle: true
  },
  auth: {
    tokenKey: 'onified-token',
    userKey: 'onified-user',
    refreshTokenKey: 'onified-refresh-token'
  },
  keycloak: {
    issuer: 'http://localhost:9090/realms/onified',
    clientId: 'onified-web-app',
    redirectUri: 'http://localhost:4200',
    postLogoutRedirectUri: 'http://localhost:4200',
    scope: 'openid profile email',
    responseType: 'code',
    showDebugInformation: true
  }
};
