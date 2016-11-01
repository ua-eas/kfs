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
import Link from './link.jsx';
import KfsUtils from '../../sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, user: {}, environment: {}, backdoorId: ""};
    },
    componentWillMount() {
        let userPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/authentication/logged-in-user";
        KfsUtils.ajaxCall({
            url: userPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function(user) {
                this.setState({user: user});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });

        let preferencesPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/preferences/institution";
        KfsUtils.ajaxCall({
            url: preferencesPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function(preferences) {
                this.setState({preferences: preferences});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });

        let environmentPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/system/environment";
        KfsUtils.ajaxCall({
            url: environmentPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function(env) {
                this.setState({environment: env});

                if (this.state.environment && this.state.environment.prodMode === false) {
                    $('.body').addClass('test-env')
                    $('#test-header').css('display', 'block')
                }
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });

        let backdoorPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/backdoor/id";
        KfsUtils.ajaxCall({
            url: backdoorPath,
            dataType: 'json',
            contentType: 'application/json',
            cache: false,
            type: 'GET',
            success: function(backdoorJson) {
                this.setState({backdoorId: backdoorJson.backdoorId});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    handleBackDoorEnter(e) {
        if (e.charCode == 13) {
            $('#backdoorLoginButton').click();
        }
    },
    backdoorLogin(event) {
        event.preventDefault();
        let path = KfsUtils.getUrlPathPrefix() + "sys/api/v1/backdoor/login";

        let backdoorId = $("#backdoorId").val();
        if (backdoorId !== '') {
            KfsUtils.ajaxCall({
                url: path,
                dataType: 'json',
                contentType: 'application/json',
                cache: false,
                data: JSON.stringify({backdoorId: backdoorId}),
                type: 'POST',
                success: function () {
                    $("#backdoorId").val("");
                    window.location.replace(KfsUtils.getUrlPathPrefix());
                }.bind(this),
                error: function (xhr, status, err) {
                    console.error(status, err.toString());
                }.bind(this)
            });
        }
    },
    backdoorLogout() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/api/v1/backdoor/logout";
        KfsUtils.ajaxCall({
            url: path,
            dataType: 'json',
            contentType: 'application/json',
            cache: false,
            type: 'GET',
            success: function() {
                window.location.replace(KfsUtils.getUrlPathPrefix());
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    openAboutModal(versions) {
        let aboutHtml = '';
        aboutHtml += '<div class="content about">';
        aboutHtml += '    <div class="modal-header">';
        aboutHtml += '        <h2>ABOUT</h2>';
        aboutHtml += '        <button type="button" data-remodal-action="close" class="close remodal-close">';
        aboutHtml += '          <span aria-hidden="true">&times;</span>';
        aboutHtml += '        </button>';
        aboutHtml += '    </div>';
        aboutHtml += '    <ul class="versions">';
        Object.keys(versions).map(key => {
            aboutHtml += '    <li>' + key + ': ' + versions[key] + '</li>';
        });
        aboutHtml += '    </ul>';
        aboutHtml += '</div>';
        $('.remodal-content').html(aboutHtml);
    },
    render() {
        let logoutButton;
        if (this.state.backdoorId && this.state.backdoorId !== '') {
            logoutButton = <button type="button" className="btn btn-default" onClick={this.backdoorLogout}>Logout</button>;
        }

        let prefs = this.state.preferences;
        let menuLinks = [];
        if (prefs.menu) {
            menuLinks = prefs.menu.map(function (link, i) {
                var target = "_blank";
                return <li key={i}><Link url={link.link} label={link.label} target={target}/></li>;
            });
        }

        if (prefs.versions) {
            menuLinks.push(<li key="about-item"><a data-remodal-target="modal" onClick={this.openAboutModal.bind(null, prefs.versions)}>About</a></li>)
        }

        let logoUrl = prefs.logoUrl;
        if (logoUrl && logoUrl.indexOf('data:') !== 0 && !logoUrl.startsWith('http')) {
            logoUrl = KfsUtils.getUrlPathPrefix() + logoUrl;
        }

        let backdoorIdAppender = KfsUtils.buildBackdoorIdAppender(this.state.backdoorId);
        let actionListLink = backdoorIdAppender(prefs.actionListUrl);
        let docSearchLink = backdoorIdAppender(prefs.docSearchUrl);
        let testEnv = <div></div>;

        if (this.state.environment && !this.state.environment.prodMode) {
            testEnv =
                <div id="test-header">
                    <div className="test-info">
                        <div className="column first">This is a test environment</div>
                        <div className="column middle">{this.state.backdoorId ? 'Impersonating: ' + this.state.backdoorId : ''}&nbsp;</div>
                        <div className="column last">
                            <input id="backdoorId" type="text" placeholder="Back Door ID" onKeyPress={this.handleBackDoorEnter}/>
                            <button type="button" id="backdoorLoginButton" className="btn btn-default" onClick={this.backdoorLogin}>Login</button>
                            {logoutButton}
                        </div>
                    </div>
                </div>
        }

        let actionListConnector = actionListLink && actionListLink.indexOf('?') !== -1 ? '&' : '?';

        return (
            <div>
                <div className="navbar-header">
                    <a className="navbar-brand" href="#d">
                        <img src={logoUrl} height="35px"/>
                        <span className="logo-right">Financials</span>
                    </a>
                </div>
                <nav className="collapse navbar-collapse">
                    <ul className="nav navbar-nav pull-right">
                        <li>
                            <a href={prefs.remoteViewUrl + '?url=' + escape(actionListLink + actionListConnector + 'documentTargetSpec=KFS:_top,*:_blank&routeLogTargetSpec=*:_self') + '&title=Action List'}>
                                <span className="glyphicon glyphicon-ok-sign"></span>Action List
                            </a>
                        </li>
                        <li>
                            <a href={prefs.remoteViewUrl + '?url=' + escape(docSearchLink + '&documentTargetSpec=KFS:_top,*:_blank&routeLogTargetSpec=*:_self&showSuperUserButton=false') + '&title=Doc Search'}>
                                <span className="glyphicon glyphicon-search"></span>Doc Search
                            </a>
                        </li>
                        <li className="dropdown">
                            <a href="#d" className="dropdown-toggle" data-toggle="dropdown" title={this.state.user.principalName}>
                                <span className="glyphicon glyphicon-user"></span>{this.state.user.firstName}&nbsp;
                                <span className="caret"></span>
                            </a>
                            <ul className="dropdown-menu pull-right">
                                <li><a href={prefs.signoutUrl}>Sign Out</a></li>
                            </ul>
                        </li>
                        <li className="dropdown">
                            <a href="#d" id="nbAcctDD" className="dropdown-toggle" data-toggle="dropdown">
                                <i className="icon-user"></i>
                                <span className="glyphicon glyphicon-menu-hamburger"></span>Menu&nbsp;
                                <span className="caret"></span>
                            </a>
                            <ul className="dropdown-menu pull-right">
                                {menuLinks}
                            </ul>
                        </li>
                    </ul>
                </nav>
                {testEnv}
            </div>
        );
    }
});

render(
    <Header/>,
    document.getElementById('header')
);

export default Header;
