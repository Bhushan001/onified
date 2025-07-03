const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  name: 'hub',

  exposes: {
    './Component': './projects/hub/src/app/hub.component.ts',
    './Dashboard': './projects/hub/src/app/dashboard-wrapper.component.ts',
    './Styles': './projects/hub/src/styles.scss',
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
