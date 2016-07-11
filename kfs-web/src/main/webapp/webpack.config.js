var webpack = require('webpack');
module.exports = {
    entry: {
        app: "./scripts/components/general/app.jsx",
        institutionconfig: "./scripts/components/institution-config/institutionConfig.jsx",
        accessdenied: "./scripts/components/general/accessDenied.jsx"
    },
    output: {
        path: __dirname + '/build',
        filename: "[name].bundle.js"
    },
    resolve: {
        modulesDirectories: ['node_modules', 'lib'],
        extensions: ['', '.js', '.css']
    },
    module: {
        preLoaders: [
            {test: /\.json$/, loader: 'json'}
        ],
        loaders: [
            { test: /\.jsx$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /institutionConfigUtils\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /utils\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /user_preferences\.js$/, loaders: ['babel','babel-loader'], exclude: /node_modules/ },
            { test: /\.css$/, loader: "style!css" }
        ]
    },
    plugins: [
        new webpack.NoErrorsPlugin()
    ]

}