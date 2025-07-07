const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({
  name: 'console',
  filename: 'remoteEntry.js',
  exposes: {
    './Dashboard': './src/app/dashboard-wrapper.component.ts',
  },
  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' })
  }
}); 