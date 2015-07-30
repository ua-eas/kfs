import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Header = React.createClass({
    getInitialState: function() {
        return {preferences: {}}
    },
    componentWillMount: function() {
        var path = KfsUtils.getUrlPathPrefix() + "core/preferences/institution"

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
    render: function() {
        var prefs = this.state.preferences
        var menuLinks = []
        if (prefs.menu) {
            menuLinks = prefs.menu.map(function (link) {
                return <li><Link url={link.link} label={link.label}/></li>
            })
        }
        return (
            <div>
                <div className="navbar-header">
                    <a className="navbar-brand" href="#">
                        <img src={prefs.logoUrl} height="40px"/>
                        <span className="institution-name">{prefs.institutionName}</span>Financials
                    </a>
                </div>
                <nav className="collapse navbar-collapse">
                    <ul className="nav navbar-nav pull-right">
                        <li>
                            <a href="#"><span className="glyphicon glyphicon-ok-sign"></span>Action List</a>
                        </li>
                        <li>
                            <a href="#"><span className="glyphicon glyphicon-search"></span>Doc Search</a>
                        </li>
                        <li className="dropdown">
                            <a href="#" className="dropdown-toggle" data-toggle="dropdown">
                                <span className="glyphicon glyphicon-user"></span>Profile
                            </a>
                            <ul className="dropdown-menu pull-right">
                                <li><a href="#">Sign Out</a></li>
                            </ul>
                        </li>
                        <li className="dropdown">
                            <a href="#" id="nbAcctDD" className="dropdown-toggle" data-toggle="dropdown">
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
