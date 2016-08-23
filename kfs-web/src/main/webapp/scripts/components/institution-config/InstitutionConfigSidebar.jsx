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
import React from 'react';
import { render } from 'react-dom';
import KfsUtils from '../../sys/utils.js';

export default class InstitutionConfigSidebar extends React.Component {
    constructor(props) {
        super(props);
        this.initialize();

        this.updateIndicator = this.updateIndicator.bind(this);
    }

    initialize() {
        if (window.location.hash === '#/' || window.location.hash === '') {
            $('#sidebar a[href="#/navigation"]').siblings(' div.indicator').addClass('active');
        }
    }

    updateIndicator(event) {
        $('#sidebar div.active').removeClass('active');
        $(event.target).siblings('div.indicator').addClass('active');
    }

    render() {
        let home = KfsUtils.getUrlPathPrefix();
        let hash = window.location.hash;
        let linkObjs = [
            {'label': 'Dashboard', 'url': home},
            {'label': 'Logo Upload', 'url': '#/logo'},
            {'label': 'Navigation Configuration', 'url': '#/navigation'},
            {'label': 'Menu Configuration', 'url': '#/menu'}
        ];
        let links = linkObjs.map((link, index) => {
            let indicator = hash === link.url ? <div className="active indicator"></div> : <div className="indicator"></div>;
            return <li key={'nav-'+index} className="panel list-item"><a href={link.url} onClick={this.updateIndicator}>{link.label}</a>{indicator}</li>;
        });
        return (
            <div className="inst-config-sidebar">
                <ul id="link-groups" className="nav list-group">
                    <li className="list-item"></li>
                    {links}
                </ul>
            </div>
        )
    }
}

render(<InstitutionConfigSidebar/>, document.getElementById('sidebar'));
