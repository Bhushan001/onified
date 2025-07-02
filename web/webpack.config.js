const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  remotes: {
    "hub": "hub@http://localhost:4300/remoteEntry.js",
    "console": "console@http://localhost:4400/remoteEntry.js",
    "workspace": "workspace@http://localhost:4500/remoteEntry.js",    
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
