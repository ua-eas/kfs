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
import React, {Component} from 'react';
import { render } from 'react-dom';
import { hashHistory, Router, Route, IndexRoute, Link } from 'react-router';

/*
    Don't remove these. It appears that they are not used but by importing
    them they create React components that are rendered into specific
    elements on the page. Without them there will be no header or sidebar/nav.
 */
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
