var Sidebar = React.createClass({
    componentDidMount: function() {
        initializeSidebar();
    },
    render: function() {
        return (
            <div id="sidebar">
                <ul id="accordion" className="nav list-group">
                    <li className="first">
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#orders-menu">
                                <span>Orders</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="orders-menu" className="sublinks collapse">
                                <a className="list-group-item small">Orders Sub A</a>
                                <a className="list-group-item small">Orders Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#trips-menu">
                                <span>Trips</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="trips-menu" className="sublinks collapse">
                                <a className="list-group-item small">Trips Sub A</a>
                                <a className="list-group-item small">Trips Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#accounting-menu">
                                <span>Accounting</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="accounting-menu" className="sublinks collapse">
                                <a className="list-group-item small">Accounting Sub A</a>
                                <a className="list-group-item small">Accounting Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#chart-menu">
                                <span>Chart of Accounts</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="chart-menu" className="sublinks collapse">
                                <a className="list-group-item small">Chart of Accounts Sub A</a>
                                <a className="list-group-item small">Chart of Accounts Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#workflow-menu">
                                <span>Workflow</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="workflow-menu" className="sublinks collapse">
                                <a className="list-group-item small">Workflow Sub A</a>
                                <a className="list-group-item small">Workflow Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-toggle="collapse" data-target="#blah-menu">
                                <span>Blah</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="blah-menu" className="sublinks collapse">
                                <a className="list-group-item small">Blah Sub A</a>
                                <a className="list-group-item small">Blah Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-toggle="collapse" data-target="#foo-menu">
                                <span>Foo Bar</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="foo-menu" className="sublinks collapse">
                                <a className="list-group-item small">Foo Bar Sub A</a>
                                <a className="list-group-item small">Foo Bar Sub B</a>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        )
    }
});

React.render(
    <Sidebar/>,
    document.getElementById('sidebar-wrapper')
);
