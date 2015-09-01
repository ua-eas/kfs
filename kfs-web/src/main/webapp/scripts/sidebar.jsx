import Link from './link.jsx';
import KfsUtils from './sys/utils.js';

let animationTime = 250

var Sidebar = React.createClass({
    getInitialState() {
        return {institutionPreferences: {}, userPreferences: {}, expandedLinkGroup: ""}
    },
    componentWillMount() {
        let institutionPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution"
        $.ajax({
            url: institutionPath,
            dataType: 'json',
            type: 'GET',
            success: function(preferences) {
                this.setState({institutionPreferences: preferences});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })

        let userPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/users/khuntley"
        $.ajax({
            url: userPath,
            dataType: 'json',
            type: 'GET',
            success: function(userPreferences) {
                this.setState({userPreferences: userPreferences});
            }.bind(this),
            error: function(xhr, status, err) {
                console.log(err.toString());
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
        let userPreferences = this.state.userPreferences;
        let sidebarOutValue = userPreferences.sidebarOut;
        console.log(sidebarOutValue);

        if (sidebarOutValue) {
            sidebarOutValue = false;
        } else {
            sidebarOutValue = true;
        }

        console.log(sidebarOutValue);

        userPreferences.sidebarOut = sidebarOutValue;
        this.setState({ userPreferences: userPreferences });

        let userPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/users/khuntley"

        $.ajax({
            url: userPath,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(this.state.userPreferences),
            type: 'PUT',
            success: function(userPreferences) {
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    render() {
        let rootPath = KfsUtils.getUrlPathPrefix()
        let linkGroups = []
        if (this.state.institutionPreferences.linkGroups) {
            let beforeActive = findLabelBeforeActive(this.state.institutionPreferences.linkGroups, this.state.expandedLinkGroup)
            let groups = this.state.institutionPreferences.linkGroups
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

        let windowWidth = $(window).width()

        if ( ! this.state.userPreferences.sidebarOut ) {
            $('#wrapper').css('left','25px').css('width','calc(100% - 25px)');
            $('#sidebar-wrapper').css('width','25px');
            $('#menu-toggle').css('left','0').css('position','fixed');
            $('#menu-toggle>span').removeClass('glyphicon-menu-left').addClass('glyphicon-menu-right');
        } else {
            $('#wrapper').css('left','320px').css('width','calc(100% - 320px)');
            $('#sidebar-wrapper').css('width','320px');
            $('#menu-toggle').css('position','inherit');
            $('#menu-toggle>span').removeClass('glyphicon-menu-right').addClass('glyphicon-menu-left');
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