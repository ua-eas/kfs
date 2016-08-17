import React from 'react';
import { render } from 'react-dom';
import Link from './link.jsx';
import KfsUtils from '../../sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, user: {}, environment: {}, backdoorId: ""};
    },
    componentWillMount() {
        let userPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/authentication/logged-in-user";
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

        let preferencesPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/institution";
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

        let environmentPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/system/environment";
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

        let backdoorPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/backdoor/id";
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
        let path = KfsUtils.getUrlPathPrefix() + "api/v1/sys/backdoor/login";

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
        let path = KfsUtils.getUrlPathPrefix() + "api/v1/sys/backdoor/logout";
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
                            <a href={prefs.remoteViewUrl + '?url=' + escape(actionListLink + '?targetSpec=KFS:_top,*:_blank') + '&title=Action List'}>
                                <span className="glyphicon glyphicon-ok-sign"></span>Action List
                            </a>
                        </li>
                        <li>
                            <a href={prefs.remoteViewUrl + '?url=' + escape(docSearchLink + '&targetSpec=KFS:_top,*:_blank') + '&title=Doc Search'}>
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
