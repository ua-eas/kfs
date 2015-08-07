import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

var Sidebar = React.createClass({
    getInitialState() {
        return {preferences: {}}
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
    toggleAccordion(event) {
        let prevActive = $("li.active")
        $("li.active").removeClass("active")
        $("li.before-active").removeClass("before-active")
        $("span.glyphicon-menu-up").toggleClass("glyphicon-menu-down glyphicon-menu-up")

        let target = event.target
        if ($(prevActive).children("a").get(0) != target) {
            $(target).find("span.indicator").toggleClass('glyphicon-menu-down glyphicon-menu-up')
            let li = $(target).closest("li")
            $(li).toggleClass("active")
            $(li).prev("li").toggleClass("before-active")
        }
    },
    toggleSidebar(event) {
        if ($('#sidebar-wrapper').width() > 5) {
            $('#wrapper').animate({'left': '5px'}, {duration: 500, queue: false});
            $('#sidebar-wrapper').animate({'width': '5px'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'none'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '99%'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'right': '5px'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '5px'}, {duration: 500, queue: false});
        } else {
            $('#wrapper').animate({'left': '25%'}, {duration: 500, queue: false});
            $('#sidebar-wrapper').animate({'width': '25%'}, {duration: 500, queue: false});
            $('#sidebar').animate({'display': 'block'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'width': '75%'}, {duration: 500, queue: false});
            $('#main-wrapper').animate({'right': '25%'}, {duration: 500, queue: false});
            $('#menu-toggle').animate({'left': '25%'}, {duration: 500, queue: false});
        }
        $('#menu-toggle>span').toggleClass('glyphicon-triangle-left glyphicon-triangle-right')
    },
    render() {
        let linkGroups = []
        if (this.state.preferences.linkGroups) {
            let groups = this.state.preferences.linkGroups
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(<LinkGroup key={i} group={groups[i]} handleClick={this.toggleAccordion}/>)
            }
        }
        return (
            <div id="sidebar">
                <div id="menu-toggle" onClick={this.toggleSidebar}><span className="glyphicon glyphicon-triangle-left"></span></div>
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
    render() {
        let label = this.props.group.label
        let id = label.toLowerCase().replace(/\s+/g, "-")
        let links = this.props.group.links.map((link, i) => {
            return <Link key={i} url={link.link} label={link.label} className="list-group-item"/>
        })

        return (
            <li className="panel">
                <a href="#d" data-parent="#accordion" data-toggle="collapse" data-target={"#" + id + "-menu"} onClick={this.props.handleClick}>
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

React.render(
    <Sidebar/>,
    document.getElementById('sidebar-wrapper')
);

export default Sidebar;