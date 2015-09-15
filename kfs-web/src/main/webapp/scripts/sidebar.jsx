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
            $('#content-overlay').removeClass('visible')
        } else {
            this.setState({expandedLinkGroup: label})
            $('#content-overlay').addClass('visible')
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
            let groups = this.state.institutionPreferences.linkGroups
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(
                    <LinkGroup key={i}
                               group={groups[i]}
                               handleClick={this.toggleAccordion}
                               expandedLinkGroup={this.state.expandedLinkGroup}/>
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
                    <li><LinkFilter/></li>
                    <li className="panel list-item"><a href={rootPath}>Dashboard</a></li>
                    {linkGroups}
                </ul>
            </div>
        )
    }
});

var filterLinks = function(links, type) {
    return links.filter(function(link) {
        return link.type === type
    }).map((link, i) => {
        let className = 'list-group-item ' + link.type
        return <Link key={type + "_" + i} url={link.link} label={link.label} className={className}/>
    })
}

var LinkGroup = React.createClass({
    render() {
        let label = this.props.group.label
        label = label.replace('&', '')
        let id = label.toLowerCase().replace(/\s+/g, "-")
        id = id.replace('&', 'and')

        let activitiesLinks = filterLinks(this.props.group.links, "activities")
        let referenceLinks = filterLinks(this.props.group.links, "reference")
        let administrationLinks = filterLinks(this.props.group.links, "administration")

        let links = []
        let headingCount = 0
        if (activitiesLinks.length > 0) {
            links = links.concat([<h4 key="activitiesLabel" className="activities">Activities</h4>]).concat(activitiesLinks)
            headingCount++
        }
        if (referenceLinks.length > 0) {
            links = links.concat([<h4 key="referencesLabel" className="reference">Reference</h4>]).concat(referenceLinks)
            headingCount++
        }
        if (administrationLinks.length > 0) {
            links = links.concat([<h4 key="administrationLabel" className="administration">Administration</h4>]).concat(administrationLinks)
            headingCount++
        }

        if (headingCount > 0) {
            headingCount--
        }

        let panelClassName = "panel list-item"
        if (this.props.expandedLinkGroup === label) {
            panelClassName += " active"
        }

        let sublinksClass = "sublinks collapse"
        if (links.length > (36 - headingCount)) {
            sublinksClass += " col-3"
        } else if (links.length > (18 - headingCount)) {
            sublinksClass += " col-2"
        }

        return (
            <li className={panelClassName}>
                <a href="#d" data-parent="#accordion" data-toggle="collapse" data-target={"#" + id + "-menu"} onClick={this.props.handleClick.bind(null, label)}>
                    <span>{label}</span>
                </a>
                <div id={id + "-menu"} className={sublinksClass}>
                    {links}
                </div>
            </li>
        )
    }
});

var LinkFilter = React.createClass({
    getInitialState() {
        return { checked: ['activities', 'reference', 'administration'] }
    },
    modifyLinkFilter(type) {
        let newChecked = this.state.checked
        let index = newChecked.indexOf(type)
        if (index === -1) {
            newChecked.push(type)
        } else {
            newChecked.splice(index, 1)
        }
        this.setState({checked: newChecked})
        $('#sidebar .' + type).toggle();
    },
    render() {
        let activitiesChecked = this.state.checked.indexOf('activities') != -1
        let referenceChecked = this.state.checked.indexOf('reference') != -1
        let administrationChecked = this.state.checked.indexOf('administration') != -1
        return (
            <div id="linkFilter">
                <input onChange={this.modifyLinkFilter.bind(null, 'activities')} type="checkbox" id="activities" value="activities" name="linkFilter" checked={activitiesChecked}/><label htmlFor="activities">Activities</label>
                <input onChange={this.modifyLinkFilter.bind(null, 'reference')} type="checkbox" id="reference" value="reference" name="linkFilter" checked={referenceChecked}/><label htmlFor="reference">Reference</label>
                <input onChange={this.modifyLinkFilter.bind(null, 'administration')} type="checkbox" id="administration" value="administration" name="linkFilter" checked={administrationChecked}/><label htmlFor="administration">Administration</label>
            </div>
        )
    }
});

React.render(
    <Sidebar/>,
    document.getElementById('sidebar')
);

export default Sidebar;