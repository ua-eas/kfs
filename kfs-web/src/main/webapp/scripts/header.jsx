var Header = React.createClass({
    render: function() {
        return (
            <div>
                <div className="navbar-header">
                    <a className="navbar-brand" href="#">
                        <img src="https://upload.wikimedia.org/wikipedia/en/thumb/5/59/POL_Jagiellonian_University_logo.svg/361px-POL_Jagiellonian_University_logo.svg.png" height="40px"/>
                        <span className="institution-name">Jagiellonian</span> Financials
                    </a>
                </div>
                <nav className="collapse navbar-collapse">
                    <ul className="nav navbar-nav pull-right">
                        <li>
                            <a href="#">
                                <span className="glyphicon glyphicon-ok-sign"></span>Action List</a>
                        </li>
                        <li>
                            <a href="#">
                                <span className="glyphicon glyphicon-search"></span>Doc Search</a>
                        </li>
                        <li>
                            <a href="#">
                                <span className="glyphicon glyphicon-user"></span>Profile</a>
                        </li>
                        <li className="dropdown">
                            <a href="#" id="nbAcctDD" className="dropdown-toggle" data-toggle="dropdown">
                                <i className="icon-user"></i>
                                <span className="glyphicon glyphicon-menu-hamburger"></span>Menu</a>
                            <ul className="dropdown-menu pull-right">
                                <li><a href="#">Something Fancy</a></li>
                                <li><a href="#">Feedback</a></li>
                                <li><a href="#">Privacy Policy</a></li>
                                <li><a href="#">Help</a></li>
                                <li><a href="#">Log Out</a></li>
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
