import React from 'react/addons';
import Router from 'react-router';
import {Route, RouteHandler, DefaultRoute, NotFoundRoute} from 'react-router';

import Header from '../../header.jsx';
import InstitutionConfigSidebar from './InstitutionConfigSidebar.jsx';

import LogoUpload from './logo/LogoUpload.jsx';
import NavigationConfig from './navigation/NavigationConfig.jsx';
import MenuConfig from './menu/MenuConfig.jsx';

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
        <Route name="logo-upload" path="/logo" handler={LogoUpload}/>
        <Route name="navigation-config" path="/navigation" handler={NavigationConfig}/>
        <Route name="menu-config" path="/menu" handler={MenuConfig}/>
        <NotFoundRoute handler={NavigationConfig}/>
    </Route>
);

Router.run(routes, function (Handler) {
    React.render(<Handler/>, document.getElementById('page-content'));
});