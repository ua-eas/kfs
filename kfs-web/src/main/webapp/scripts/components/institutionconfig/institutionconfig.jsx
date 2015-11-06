import React from 'react/addons';
import Router from 'react-router';
import {Route, RouteHandler, DefaultRoute, NotFoundRoute} from 'react-router';

import Header from '../../header.jsx';
import Footer from '../../footer.jsx';
import InstConfigSidebar from './institutionConfigSidebar.jsx';

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
    <Route handler={App} name="app" path="/">
        <DefaultRoute handler={NavigationConfig} />
        <Route name="navigationconfig" path="/navigationconfig" handler={NavigationConfig}/>
        <Route name="menuconfig" path="/menuconfig" handler={MenuConfig}/>
        <NotFoundRoute handler={NavigationConfig}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('page-content'));
});