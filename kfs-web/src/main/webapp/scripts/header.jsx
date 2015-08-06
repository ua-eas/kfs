import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, backdoorId: "", backdoorIdField: "", backdoorIdAppender: new KfsUtils.BackdoorIdAppender()}
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
                    this.setState({backdoorId: backdoorJson.backdoorId});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(status, err.toString());
                }.bind(this)
            })
        );
    },
    handleBackDoorIdChange(e) {
        let s = this.state
        s.backdoorIdField = e.target.value
        this.setState(s)
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
            menuLinks = prefs.menu.map(function (link, i) {
                let backdoorAppendedLink = backdoorIdAppender.appendBackdoorId(link.link)
                var target = "_blank"
                return <li key={i}><Link url={backdoorAppendedLink} label={link.label} target={target}/></li>
            })
        }

        let actionListLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.actionListUrl)
        let docSearchLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.docSearchUrl)

        let profileIcon = "glyphicon glyphicon-user"
        let backdoorClass = "dropdown"
        let backdoorMessage;
        let profileTitle = "Profile";
        let logoutButton;
        if (this.state.backdoorId && this.state.backdoorId !== '') {
            profileIcon = "glyphicon glyphicon-king"
            backdoorClass += " backdoor"
            backdoorMessage = <li>Logged in as: {this.state.backdoorId}</li>
            profileTitle = "Logged in as: " + this.state.backdoorId
            logoutButton = <button type="button" className="btn btn-default" onClick={this.backdoorLogout}>Logout</button>
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
                                <span className={profileIcon}></span>Profile
                            </a>
                            <ul className="dropdown-menu pull-right">
                                {backdoorMessage}
                                <li>
                                    <form>
                                        <input id="backdoorId" type="text" placeholder="Back Door ID" onChange={this.handleBackDoorIdChange}/>
                                        <div className="btn-group" role="group">
                                            <button type="button" className="btn btn-default" onClick={this.backdoorLogin} disabled={ ! this.state.backdoorIdField }>Login</button>
                                            {logoutButton}
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
