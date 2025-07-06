const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  name: 'console',

  exposes: {
    './Component': './projects/console/src/app/console.component.ts',
    './Dashboard': './projects/console/src/app/dashboard-wrapper.component.ts'
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
