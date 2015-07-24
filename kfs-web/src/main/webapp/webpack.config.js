var webpack = require('webpack');
module.exports = {
    entry: {
        header: "./scripts/header.jsx",
        sidebar: "./scripts/sidebar.jsx"
    },
    output: {
        path: __dirname + '/build',
        filename: "[name]-bundle.js"
    },
    module: {
        loaders: [
            { test: /\.jsx$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /\.css$/, loader: "style!css" }
        ]
    },
    plugins: [
        new webpack.NoErrorsPlugin()
    ]

}