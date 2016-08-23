/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
