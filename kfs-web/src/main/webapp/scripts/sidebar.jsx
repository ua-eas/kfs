var Sidebar = React.createClass({
    getInitialState: function() {
        return { activeTabs: []};
    },
    handleClick: function(name, event) {
        var activeTabs = this.state.activeTabs
        var index = activeTabs.indexOf(name)
        if (index < 0) {
            activeTabs.push(name)
        } else {
            activeTabs.splice(index, 1)
        }
        this.setState({activeTabs: activeTabs})
    },
    render: function() {
        return (
            <div id="sidebar">
                <ul className="nav list-group">
                    <li className="first">
                    </li>
                    <li className={(this.state.activeTabs.indexOf("second") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#orders-menu" onClick={this.handleClick.bind(null, "second")}>
                                <span>Orders</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="orders-menu" className="sublinks collapse">
                                <a className="list-group-item small">Orders Sub A</a>
                                <a className="list-group-item small">Orders Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("third") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#trips-menu" onClick={this.handleClick.bind(null, "third")}>
                                <span>Trips</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="trips-menu" className="sublinks collapse">
                                <a className="list-group-item small">Trips Sub A</a>
                                <a className="list-group-item small">Trips Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("fourth") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#accounting-menu" onClick={this.handleClick.bind(null, "fourth")}>
                                <span>Accounting</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="accounting-menu" className="sublinks collapse">
                                <a className="list-group-item small">Accounting Sub A</a>
                                <a className="list-group-item small">Accounting Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("fifth") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#chart-menu" onClick={this.handleClick.bind(null, "fifth")}>
                                <span>Chart of Accounts</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="chart-menu" className="sublinks collapse">
                                <a className="list-group-item small">Chart of Accounts Sub A</a>
                                <a className="list-group-item small">Chart of Accounts Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("sixth") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#workflow-menu" onClick={this.handleClick.bind(null, "sixth")}>
                                <span>Workflow</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="workflow-menu" className="sublinks collapse">
                                <a className="list-group-item small">Workflow Sub A</a>
                                <a className="list-group-item small">Workflow Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("seventh") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#blah-menu" onClick={this.handleClick.bind(null, "seventh")}>
                                <span>Blah</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
                            </a>

                            <div id="blah-menu" className="sublinks collapse">
                                <a className="list-group-item small">Blah Sub A</a>
                                <a className="list-group-item small">Blah Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className={(this.state.activeTabs.indexOf("eighth") < 0) ? "inactive": "active"}>
                        <div className="nav-wrapper">
                            <a className="list-group-item" href="#" data-toggle="collapse" data-target="#foo-menu" onClick={this.handleClick.bind(null, "eighth")}>
                                <span>Foo Bar</span>
                                <span className="glyphicon glyphicon-menu-down pull-right"></span>
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
