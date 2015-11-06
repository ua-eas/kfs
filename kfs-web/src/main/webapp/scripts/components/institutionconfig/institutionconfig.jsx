import React from 'react/addons';
import Router from 'react-router';
import {Route, RouteHandler} from 'react-router';

import NavigationConfig from './navigationconfig.jsx';
import MenuConfig from './menuconfig.jsx';

let App = React.createClass({
    render: function() {
        return (
            <div>
                <RouteHandler/>
            </div>
        )
    }
});

let routes = (
    <Route handler={App}>
        <Route name="navigationconfig" path="/" handler={NavigationConfig}/>
        <Route name="menuconfig" path="/menuconfig" handler={MenuConfig}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('page-content'));
});