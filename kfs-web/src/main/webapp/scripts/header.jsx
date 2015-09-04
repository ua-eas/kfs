import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, user: {}}
    },
    componentWillMount() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution"
        let userPath = KfsUtils.getUrlPathPrefix() + "sys/authentication/loggedInUser"

        $.ajax({
            url: userPath,
            dataType: 'json',
            type: 'GET',
            success: function(user) {
                this.setState({user: user});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
        $.ajax({
            url: path,
            dataType: 'json',
            type: 'GET',
            success: function(preferences) {
                this.setState({preferences: preferences});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    render() {
        let prefs = this.state.preferences
        let menuLinks = []
        if (prefs.menu) {
            menuLinks = prefs.menu.map(function (link, i) {
                var target = "_blank"
                return <li key={i}><Link url={link.link} label={link.label} target={target}/></li>
            })
        }

        let logoUrl = prefs.logoUrl
        if (logoUrl && !logoUrl.startsWith('http')) {
            logoUrl = KfsUtils.getUrlPathPrefix() + logoUrl;
        }


        let backdoorIdAppender = KfsUtils.buildBackdoorIdAppender(this.state.backdoorId)
        let actionListLink = backdoorIdAppender(prefs.actionListUrl)
        let docSearchLink = backdoorIdAppender(prefs.docSearchUrl)
        return (
            <div>
                <div>
                    <div className="navbar-header">
                        <a className="navbar-brand" href="#d">
                            <img src={logoUrl} height="40px" width="40px"/>
                            <span className="institution-name">{prefs.institutionName}</span>Financials
                        </a>
                    </div>
                    <nav className="collapse navbar-collapse">
                        <ul className="nav navbar-nav pull-right">
                            <li>
                                <a href={actionListLink} target="_blank"><span className="glyphicon glyphicon-ok-sign"></span>Action List</a>
                            </li>
                            <li>
                                <a href={docSearchLink} target="_blank"><span className="glyphicon glyphicon-search"></span>Doc Search</a>
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
                </div>
            </div>
        );
    }
});

React.render(
    <Header/>,
    document.getElementById('header')
);

export default Header;
