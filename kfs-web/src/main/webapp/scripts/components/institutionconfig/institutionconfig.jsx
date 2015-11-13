import React from 'react/addons';
import Router from 'react-router';
import {Route, RouteHandler, DefaultRoute, NotFoundRoute} from 'react-router';

import Header from '../../header.jsx';
import Footer from '../../footer.jsx';
import InstConfigSidebar from './institutionConfigSidebar.jsx';

import NavigationConfig from './navigation/navigationconfig.jsx';
import MenuConfig from './menu/menuconfig.jsx';

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
        <Route name="navigationconfig" path="/navigation" handler={NavigationConfig}/>
        <Route name="menuconfig" path="/menu" handler={MenuConfig}/>
        <NotFoundRoute handler={NavigationConfig}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('page-content'));
});