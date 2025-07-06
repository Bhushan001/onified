const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  name: 'workspace',

  exposes: {
    './Component': './projects/workspace/src/app/workspace.component.ts',
    './Dashboard': './projects/workspace/src/app/dashboard-wrapper.component.ts'
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
