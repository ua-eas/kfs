import Link from './link.jsx';
import UserPrefs from './sys/user_preferences.js';
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

        let thisComponent = this;
        UserPrefs.getUserPreferences(function (userPreferences) {
            thisComponent.setState({userPreferences: userPreferences});
        }, function (error) {
            console.log("error getting preferences: " + error);
        });
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
        $('#menu-toggle').toggleClass('rotated');
        $('#sidebar').toggleClass('collapsed');

        let userPreferences = this.state.userPreferences;
        let sidebarOutValue = ! userPreferences.sidebarOut;

        userPreferences.sidebarOut = sidebarOutValue;
        this.setState({ userPreferences: userPreferences });

        UserPrefs.putUserPreferences(userPreferences);
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

        let menuToggleClassName = "glyphicon glyphicon-menu-left"
        if (this.state.userPreferences.sidebarOut === false) {
            menuToggleClassName += " rotated"
            $('#sidebar').addClass('collapsed');
        }

        return (
            <div>
                <ul id="accordion" className="nav list-group accordion accordion-group">
                    <li onClick={this.toggleSidebar}><span id="menu-toggle" className={menuToggleClassName}></span></li>
                    <li className="panel list-item"><a href={rootPath}>Dashboard</a></li>
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

        let panelClassName = "panel list-item"
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
    document.getElementById('sidebar')
);

export default Sidebar;