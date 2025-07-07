const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  name: 'workspace',
  filename: 'remoteEntry.js',
  exposes: {
    './Dashboard': './src/app/dashboard-wrapper.component.ts',
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' })
  }
}); 