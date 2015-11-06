var webpack = require('webpack');
module.exports = {
    entry: {
        app: "./scripts/app.jsx",
        sidebar: "./scripts/sidebar.jsx",
        institutionconfigsidebar: './scripts/components/institutionconfig/institutionconfigsidebar.jsx',
        institutionconfig: "./scripts/components/institutionconfig/institutionconfig.jsx",
        accessdenied: "./scripts/components/general/accessDenied.jsx"
    },
    output: {
        path: __dirname + '/build',
        filename: "[name].bundle.js"
    },
    module: {
        loaders: [
            { test: /\.jsx$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /institutionconfigutils\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /utils\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /user_preferences\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /\.css$/, loader: "style!css" }
        ]
    },
    plugins: [
        new webpack.NoErrorsPlugin()
    ]

}