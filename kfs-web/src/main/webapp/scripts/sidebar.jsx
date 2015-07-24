import CustomLink from './customlink.jsx';

var Sidebar = React.createClass({
    getInitialState: function() {
        return {preferences: {}}
    },
    componentWillMount: function() {
        $.ajax({
            url: "/kfs-dev/core/preferences/institution",
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
    toggleAccordion: function(event) {
        var prevActive = $("li.active")
        $("li.active").removeClass("active")
        $("li.before-active").removeClass("before-active")
        $("span.glyphicon-menu-up").toggleClass("glyphicon-menu-down glyphicon-menu-up")

        var target = event.target
        if ($(prevActive).children("a").get(0) != target) {
            $(target).find("span.indicator").toggleClass('glyphicon-menu-down glyphicon-menu-up')
            var li = $(target).closest("li")
            $(li).toggleClass("active")
            $(li).prev("li").toggleClass("before-active")
        }
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
        var linkGroups = []
        if (this.state.preferences.linkGroups) {
            console.log("got some linkgroups")
            var groups = this.state.preferences.linkGroups
            for (var i = 0; i < groups.length; i++) {
                linkGroups.push(<LinkGroup group={groups[i]} handleClick={this.toggleAccordion}/>)
            }
        }
        return (
            <div id="sidebar">
                <div id="menu-toggle" onClick={this.toggleSidebar}><span className="glyphicon glyphicon-menu-hamburger"></span></div>
                <ul id="accordion" className="nav list-group accordion accordion-group">
                    <li className="first">
                    </li>
                    {linkGroups}
                </ul>
            </div>
        )
    }
});

var LinkGroup = React.createClass({
    render: function() {
        var label = this.props.group.label
        var id = label.toLowerCase().replace(" ", "-")
        var links = this.props.group.links.map(function(link) {
            if (link.type && link.type === "custom") {
                return <CustomLink url={link.link} label={link.label} className="list-group-item"/>
            } else {
                return <DocLink link={link.link}/>
            }
        })

        return (
            <li className="panel">
                <a href="#" data-parent="#accordion" data-toggle="collapse" data-target={"#" + id + "-menu"} onClick={this.props.handleClick.bind(this)}>
                    <span>{label}</span>
                    <span className="indicator glyphicon glyphicon-menu-down pull-right"></span>
                </a>
                <div id={id + "-menu"} className="sublinks collapse">
                    {links}
                </div>
            </li>
        )
    }
});

var DocLink = React.createClass({
    render: function() {
        return (
            <a href={this.props.url} className="list-group-item">Derive from Doc</a>
        )
    }
})

React.render(
    <Sidebar/>,
    document.getElementById('sidebar-wrapper')
);

export default Sidebar;