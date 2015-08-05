import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, backdoorId: "", backdoorIdAppender: new KfsUtils.BackdoorIdAppender()}
    },
    componentWillMount() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution"
        let backdoorPath = KfsUtils.getUrlPathPrefix() + "sys/backdoor/id"

        $.when(
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
            }),
            $.ajax({
                url: backdoorPath,
                dataType: 'json',
                contentType: 'application/json',
                type: 'GET',
                success: function(backdoorJson) {
                    console.log(backdoorJson)
                    console.log(backdoorJson.backdoorId)
                    this.setState({backdoorId: backdoorJson.backdoorId});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(status, err.toString());
                }.bind(this)
            })
        );
    },
    backdoorLogin() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/backdoor/login"
        console.log(path)
        $.ajax({
            url: path,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({backdoorId: $("#backdoorId").val()}),
            type: 'POST',
            success: function() {
                $("#backdoorId").val("")
                window.location.reload()
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    backdoorLogout() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/backdoor/logout"
        $.ajax({
            url: path,
            dataType: 'json',
            contentType: 'application/json',
            type: 'GET',
            success: function() {
                window.location.reload()
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
            let backdoorIdAppender = this.state.backdoorIdAppender
            menuLinks = prefs.menu.map(function (link) {
                let backdoorAppendedLink = backdoorIdAppender.appendBackdoorId(link.link)
                var target = "_blank"
                return <li><Link url={backdoorAppendedLink} label={link.label} target={target}/></li>
            })
        }

        let actionListLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.actionListUrl)
        let docSearchLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.docSearchUrl)

        let backdoorClass = "dropdown"
        let backdoorMessage;
        let profileTitle = "Profile";
        if (this.state.backdoorId && this.state.backdoorId !== '') {
            backdoorClass += " backdoor"
            backdoorMessage = <li>Logged in as: {this.state.backdoorId}</li>
            profileTitle = "Logged in as: " + this.state.backdoorId
        }
        return (
            <div>
                <div className="navbar-header">
                    <a className="navbar-brand" href="#d">
                        <img src={prefs.logoUrl} height="40px"/>
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
                        <li className={backdoorClass}>
                            <a href="#d" className="dropdown-toggle" data-toggle="dropdown" title={profileTitle}>
                                <span className="glyphicon glyphicon-user"></span>Profile
                            </a>
                            <ul className="dropdown-menu pull-right">
                                {backdoorMessage}
                                <li>
                                    <form>
                                        <input id="backdoorId" type="text"/>
                                        <div className="btn-group" role="group">
                                            <button type="button" className="btn btn-default" onClick={this.backdoorLogin}>Login</button>
                                            <button type="button" className="btn btn-default" onClick={this.backdoorLogout}>Logout</button>
                                        </div>
                                    </form>
                                </li>
                                <li><a href={prefs.signoutUrl}>Sign Out</a></li>
                            </ul>
                        </li>
                        <li className="dropdown">
                            <a href="#d" id="nbAcctDD" className="dropdown-toggle" data-toggle="dropdown">
                                <i className="icon-user"></i>
                                <span className="glyphicon glyphicon-menu-hamburger"></span>Menu
                            </a>
                            <ul className="dropdown-menu pull-right">
                                {menuLinks}
                            </ul>
                        </li>
                    </ul>
                </nav>
            </div>
        );
    }
});

React.render(
    <Header/>,
    document.getElementById('header')
);

export default Header;
