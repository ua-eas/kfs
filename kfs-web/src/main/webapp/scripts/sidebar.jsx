var Sidebar = React.createClass({
    handleClick: function(event) {
        $(event.target).find("span.indicator").toggleClass('glyphicon-menu-down glyphicon-menu-up')
        var li = $(event.target).closest("li")
        $(li).toggleClass("active")
        $(li).prev("li").toggleClass("before-active")
    },
    toggleSidebar: function() {
        if ($('#sidebar-wrapper').width() > 5) {
            $('#sidebar-wrapper').animate({'width': '5px'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'none'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '100%'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '5px'}, {duration: 500, queue: false});
        } else {
            $('#sidebar-wrapper').animate({'width': '25%'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'block'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '75%'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '25%'}, {duration: 500, queue: false});
        }
    },
    render: function() {
        return (
            <div id="sidebar">
                <div id="menu-toggle" onClick={this.toggleSidebar}><span className="glyphicon glyphicon-menu-hamburger"></span></div>
                <ul id="accordion" className="nav list-group accordion accordion-group">
                    <li className="first">
                    </li>
                    <li>
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#orders-menu" onClick={this.handleClick}>
                                <span>Orders</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="orders-menu" className="sublinks collapse">
                                <a className="list-group-item small">Orders Sub A</a>
                                <a className="list-group-item small">Orders Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className="accordion-group">
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#trips-menu" onClick={this.handleClick}>
                                <span>Trips</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="trips-menu" className="sublinks collapse">
                                <a className="list-group-item small">Trips Sub A</a>
                                <a className="list-group-item small">Trips Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className="accordion-group">
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#accounting-menu" onClick={this.handleClick}>
                                <span>Accounting</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="accounting-menu" className="sublinks collapse">
                                <a className="list-group-item small">Accounting Sub A</a>
                                <a className="list-group-item small">Accounting Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className="accordion-group">
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#chart-menu" onClick={this.handleClick}>
                                <span>Chart of Accounts</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="chart-menu" className="sublinks collapse">
                                <a className="list-group-item small">Chart of Accounts Sub A</a>
                                <a className="list-group-item small">Chart of Accounts Sub B</a>
                            </div>
                        </div>
                    </li>
                    <li className="accordion-group">
                        <div className="nav-wrapper">
                            <a className="list-group-item accordion-toggle" href="#" data-parent="#accordion" data-toggle="collapse" data-target="#workflow-menu" onClick={this.handleClick}>
                                <span>Workflow</span>
                                <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                            </a>
                            <div id="workflow-menu" className="sublinks collapse">
                                <a className="list-group-item small">Workflow Sub A</a>
                                <a className="list-group-item small">Workflow Sub B</a>
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
