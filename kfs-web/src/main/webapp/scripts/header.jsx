import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Header = React.createClass({
    getInitialState() {
        return {preferences: {}, backdoorIdAppender: new KfsUtils.BackdoorIdAppender()}
    },
    componentWillMount() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution"

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
            let backdoorIdAppender = this.state.backdoorIdAppender
            menuLinks = prefs.menu.map(function (link) {
                let backdoorAppendedLink = backdoorIdAppender.appendBackdoorId(link.link)
                var target = "_blank"
                return <li><Link url={backdoorAppendedLink} label={link.label} target={target}/></li>
            })
        }

        let actionListLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.actionListUrl)
        let docSearchLink = this.state.backdoorIdAppender.appendBackdoorId(prefs.docSearchUrl)

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
                        <li className="dropdown">
                            <a href="#d" className="dropdown-toggle" data-toggle="dropdown">
                                <span className="glyphicon glyphicon-user"></span>Profile
                            </a>
                            <ul className="dropdown-menu pull-right">
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