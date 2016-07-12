import React, {Component} from 'react';
import { render } from 'react-dom';
import { hashHistory, Router, Route, IndexRoute, Link } from 'react-router';

import Header from '../general/header.jsx';
import InstitutionConfigSidebar from './InstitutionConfigSidebar.jsx';

import LogoUpload from './logo/LogoUpload.jsx';
import NavigationConfig from './navigation/NavigationConfig.jsx';
import MenuConfig from './menu/MenuConfig.jsx';

class App extends Component {
    render() {
        return (
            <Router history={hashHistory}>
                <Route name="logo-upload" path="/logo" component={LogoUpload}/>
                <Route name="navigation-config" path="/navigation" component={NavigationConfig}/>
                <Route name="menu-config" path="/menu" component={MenuConfig}/>
                <Route name="default" path="*" component={NavigationConfig}/>
            </Router>
        )
    }
}

render(<App />, document.getElementById('page-content'));