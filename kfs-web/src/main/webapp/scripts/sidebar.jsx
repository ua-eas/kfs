import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

let animationTime = 250

var Sidebar = React.createClass({
    getInitialState() {
        return {preferences: {}, expandedLinkGroup: ""}
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
    toggleAccordion(label) {
        let curExpandedGroup = this.state.expandedLinkGroup
        if (curExpandedGroup === label) {
            this.setState({expandedLinkGroup: ""})
        } else {
            this.setState({expandedLinkGroup: label})
        }
    },
    toggleSidebar() {
        let windowWidth = $(window).width()
        if ($('#sidebar-wrapper').width() > 25) {
            $('#wrapper').animate({'left': '25px'}, {duration: animationTime, queue: false});
            $('#wrapper').animate({'width': windowWidth - 25 + 'px'}, {duration: animationTime, queue: false, complete: function() {
                $('#wrapper').css('width', 'calc(100% - 25px)')
            }});
            $('#sidebar-wrapper').animate({'width': '25px'}, {duration: animationTime, queue: false});
            $('#menu-toggle').animate({'left': '0'}, {duration: animationTime, queue: false});
            $('#menu-toggle').css('position', 'fixed');
        } else {
            $('#wrapper').animate({'left': '320px'}, {duration: animationTime, queue: false});
            $('#wrapper').animate({'width': windowWidth - 320 + 'px'}, {duration: animationTime, queue: false, complete: function() {
                $('#wrapper').css('width', 'calc(100% - 320px)')
            }});
            $('#sidebar-wrapper').animate({'width': '320px'}, {duration: animationTime, queue: false});
            $('#menu-toggle').css('position', 'inherit');
        }
        $('#menu-toggle>span').toggleClass('glyphicon-menu-left glyphicon-menu-right')
    },
    render() {
        let rootPath = KfsUtils.getUrlPathPrefix()
        let linkGroups = []
        if (this.state.preferences.linkGroups) {
            let beforeActive = findLabelBeforeActive(this.state.preferences.linkGroups, this.state.expandedLinkGroup)
            let groups = this.state.preferences.linkGroups
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(
                    <LinkGroup key={i}
                               group={groups[i]}
                               handleClick={this.toggleAccordion}
                               expandedLinkGroup={this.state.expandedLinkGroup}
                               beforeActive={beforeActive}/>
                )
            }
        }
        return (
            <div id="sidebar">
                <div id="menu-toggle" onClick={this.toggleSidebar}><span className="glyphicon glyphicon-menu-left"></span></div>
                <ul id="accordion" className="nav list-group accordion accordion-group">
                    <li className="first"><a href={rootPath}>Dashboard</a></li>
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

        let panelClassName = "panel"
        let indicatorClassName = "indicator glyphicon pull-right"
        if (this.props.expandedLinkGroup === label) {
            panelClassName += " active"
            indicatorClassName += " glyphicon-menu-up"
        } else {
            if (this.props.beforeActive === label) {
                panelClassName += " before-active"
            }
            indicatorClassName += " glyphicon-menu-down"
        }

        return (
            <li className={panelClassName}>
                <a href="#d" data-parent="#accordion" data-toggle="collapse" data-target={"#" + id + "-menu"} onClick={this.props.handleClick.bind(null, label)}>
                    <span>{label}</span>
                    <span className={indicatorClassName}></span>
                </a>
                <div id={id + "-menu"} className="sublinks collapse">
                    {links}
                </div>
            </li>
        )
    }
});

function findLabelBeforeActive(linkGroups, expandedLinkGroup) {
    for (let i = 0; i < linkGroups.length; i++) {
        if (linkGroups[i].label === expandedLinkGroup && i > 0) {
            return linkGroups[i-1].label
        }
    }
}

React.render(
    <Sidebar/>,
    document.getElementById('sidebar-wrapper')
);

export default Sidebar;