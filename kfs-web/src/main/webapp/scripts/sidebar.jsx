import Link from './link.jsx';
import UserPrefs from './sys/user_preferences.js';
import KfsUtils from './sys/utils.js';

let animationTime = 250

var Sidebar = React.createClass({
    getInitialState() {
        let userPreferences = {};
        userPreferences.checkedLinkFilters = ["activities", "reference", "administration"];
        return { principalName: "", institutionPreferences: {}, userPreferences: userPreferences, expandedLinkGroup: "", expandedSearch: false};
    },
    componentWillMount() {
        let thisComponent = this
        let found = false

        UserPrefs.getPrincipalName(function(principalName) {
            thisComponent.setState({principalName: principalName})
            let preferencesString = localStorage.getItem("institutionPreferences")
            if ( preferencesString != null ) {
                let sessionId = KfsUtils.getKualiSessionId()
                let prefs = JSON.parse(preferencesString)
                if ( (prefs.sessionId == sessionId) && (prefs.principalName == principalName) ) {
                    found = true
                    thisComponent.setState({institutionPreferences: prefs})
                } else {
                    localStorage.removeItem("institutionPreferences")
                }
            }

            if ( ! found ) {
                let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution_links/" + principalName
                $.ajax({
                    url: institutionLinksPath,
                    dataType: 'json',
                    type: 'GET',
                    success: function (preferences) {
                        thisComponent.setState({institutionPreferences: preferences});
                        preferences.sessionId = KfsUtils.getKualiSessionId()
                        localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                    }.bind(this),
                    error: function (xhr, status, err) {
                        console.error(status, err.toString());
                    }.bind(this)
                })
            }
        },function() {
            console.error("Error retrieving principalName")
        })

        UserPrefs.getUserPreferences(function (userPreferences) {
            thisComponent.setState({userPreferences: userPreferences});
        }, function (error) {
            console.log("error getting preferences: " + error);
        });
    },
    modifyLinkFilter(type) {
        let userPreferences = this.state.userPreferences;
        let newChecked = userPreferences.checkedLinkFilters;
        let index = newChecked.indexOf(type);
        if (index === -1) {
            newChecked.push(type);
        } else {
            newChecked.splice(index, 1);
        }
        this.setState({userPreferences: userPreferences});
        UserPrefs.putUserPreferences(userPreferences);
    },
    toggleLinkGroup(label) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: ""})
            $('#content-overlay').removeClass('visible')
            $('html').off('click','**')
        } else {
            this.setState({expandedLinkGroup: label})
            $('#content-overlay').addClass('visible')
            let sidebar = this
            $('html').on('click',function(event) {
                if (!$(event.target).closest('li.panel.active').length && !$(event.target).closest('#linkFilter').length) {
                    $('li.panel.active').removeClass('active')
                    $('#content-overlay').removeClass('visible')
                    sidebar.setState({expandedLinkGroup: ""})
                }
            });
        }
    },
    toggleSidebar() {
        $('#menu-toggle').toggleClass('rotated');
        $('#sidebar').toggleClass('collapsed');

        let userPreferences = this.state.userPreferences;
        let sidebarOutValue = ! userPreferences.sidebarOut;

        userPreferences.sidebarOut = sidebarOutValue;
        this.setState({ userPreferences: userPreferences, expandedLinkGroup: "" });

        UserPrefs.putUserPreferences(userPreferences);
    },
    refreshLinks() {
        let thisComponent = this
        let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/institution_links/" + thisComponent.state.principalName

        $('.cover').show()
        $('.sidebar-waiting').css('top',($(window).height() / 2) - 20).show()

        $.ajax({
            url: institutionLinksPath,
            dataType: 'json',
            type: 'GET',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('cache-control', 'must-revalidate')
            },
            success: function (preferences) {
                thisComponent.setState({institutionPreferences: preferences});
                preferences.sessionId = KfsUtils.getKualiSessionId();
                localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                $('.cover').hide();
                $('.sidebar-waiting').hide()
            }.bind(this),
            error: function (xhr, status, err) {
                $('#sidebar').removeClass('sidebar-dim');
                console.error(status, err.toString())
            }.bind(this)
        })
    },
    autocompleteSearch(event) {
        let searchString = event.target.value;
        let expandedSearch = searchString.length > 0;

        let newState = {'search': searchString, 'expandedSearch': expandedSearch};

        if (!expandedSearch) {
            $('#content-overlay').removeClass('visible');
            $('html').off('click','**')
        } else {
            $('#content-overlay').addClass('visible');
            $('html').on('click', event => {
                if (!$(event.target).closest('li.panel.active').length && !$(event.target).closest('#linkFilter').length) {
                    $('li.panel.active').removeClass('active');
                    $('#content-overlay').removeClass('visible');
                    this.setState({expandedSearch: false});
                }
            });

            let results = {};
            let lowerSearchString = searchString.toLowerCase();
            this.state.institutionPreferences.linkGroups.forEach(linkGroup => {
                let groupResults = [];
                let groupLinks = linkGroup.links;
                for (let groupLinkType of Object.keys(groupLinks)) {
                    let linksOfType = groupLinks[groupLinkType];
                    let filteredLinks = linksOfType.filter(link => link.label.toLowerCase().indexOf(lowerSearchString) != -1);
                    groupResults = groupResults.concat(filteredLinks);
                }
                if (groupResults.length > 0) {
                    results[linkGroup.label] = groupResults;
                }
            });

            newState.searchResults = results;
        }

        this.setState(newState);
    },
    clearSearch() {
        this.setState({'search': ''});
        this.refs.searchBox.getDOMNode().focus();
    },
    render() {
        let rootPath = KfsUtils.getUrlPathPrefix();
        let linkGroups = [];
        if (this.state.institutionPreferences.linkGroups) {
            let groups = this.state.institutionPreferences.linkGroups
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(
                    <LinkGroup key={i}
                        group={groups[i]}
                        handleClick={this.toggleLinkGroup}
                        expandedLinkGroup={this.state.expandedLinkGroup}
                        checkedLinkFilters={this.state.userPreferences.checkedLinkFilters}/>
                )
            }
        }

        let menuToggleClassName = "glyphicon glyphicon-menu-left"
        if (this.state.userPreferences.sidebarOut === false) {
            menuToggleClassName += " rotated"
            $('#sidebar').addClass('collapsed');
        }

        let navSearchClass = "search list-item panel";
        if (this.state.expandedSearch) {
            navSearchClass += " active";
        }

        let searchResultsClass;
        let searchResults = 'No results found';
        if (this.state.searchResults) {
            let finalLinks = [];
            let groupCount = 0;
            for (let resultGroup of Object.keys(this.state.searchResults)) {
                let displayLinks = convertLinks(this.state.searchResults[resultGroup], 'navSearch');
                finalLinks = finalLinks.concat(addHeading(displayLinks, resultGroup));
                groupCount++;
            }
            searchResults = <div>{finalLinks}</div>;

            if (groupCount > 0) {
                groupCount--
            }

            searchResultsClass = determineSublinkClass(finalLinks, groupCount)
        }

        return (
            <div>
                <div className="cover"></div>
                <div className="sidebar-waiting"><span className="waiting-icon glyphicon glyphicon-hourglass"></span></div>
                <ul id="linkgroups" className="nav list-group">
                    <li onClick={this.toggleSidebar}><span id="menu-toggle" className={menuToggleClassName}></span></li>
                    <li className={navSearchClass}>
                        <input type="search" placeholder="Search" onChange={this.autocompleteSearch} value={this.state.search} ref="searchBox" onFocus={this.autocompleteSearch} />
                        <span className="glyphicon glyphicon-remove" onClick={this.clearSearch}></span>
                        <div className={searchResultsClass}>
                            {searchResults}
                        </div>
                    </li>
                    <li className="list-item"><LinkFilter checkedLinkFilters={this.state.userPreferences.checkedLinkFilters} modifyLinkFilter={this.modifyLinkFilter} /></li>
                    <li className="panel list-item"><a href={rootPath}>Dashboard</a></li>
                    {linkGroups}
                </ul>
                <div className="refresh">
                    Missing something? <a href="#d" onClick={this.refreshLinks}><span> Refresh Menu </span></a> to make sure your permissions are up to date.
                </div>
            </div>
        )
    }
});


var convertLinks = function(links, type) {
    if (!links) {
        return "";
    }
    return links.map((link, i) => {
        let target = link.linkType === 'kfs' ? null : '_blank';
        return <Link key={type + "_" + i} url={link.link} label={link.label} className="list-group-item" target={target}/>
    })
}

var buildDisplayLinks = function(links, type, checkedLinkFilters) {
    let displayLinks = [];
    if (links && links[type] && checkedLinkFilters && checkedLinkFilters.indexOf(type) != -1) {
        displayLinks = convertLinks(links[type], type);
    }
    return displayLinks;
}

var addHeading = function(links, type) {
    let newLinks = [];
    if (links.length > 0) {
        newLinks = newLinks.concat([<h4 key={type + "Label"}>{type}</h4>]).concat(links);
    }
    return newLinks;
}

var determineSublinkClass = function(links, headingCount) {
    let sublinksClass = "sublinks collapse";
    if (links.length > (36 - headingCount)) {
        sublinksClass += " col-3";
    } else if (links.length > (18 - headingCount)) {
        sublinksClass += " col-2";
    }
    return sublinksClass;
}

var determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "panel list-item";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName;
}

var LinkGroup = React.createClass({
    render() {
        let label = this.props.group.label
        let id = KfsUtils.buildKeyFromLabel(label)

        let activitiesLinks = buildDisplayLinks(this.props.group.links, 'activities', this.props.checkedLinkFilters)
        let referenceLinks = buildDisplayLinks(this.props.group.links, 'reference', this.props.checkedLinkFilters)
        let administrationLinks = buildDisplayLinks(this.props.group.links, 'administration', this.props.checkedLinkFilters)

        let links = addHeading(activitiesLinks, 'Activities')
        links = links.concat(addHeading(referenceLinks, 'Reference'))
        links = links.concat(addHeading(administrationLinks, 'Administration'))

        let headingCount = links.length - (activitiesLinks.length + referenceLinks.length + administrationLinks.length)
        if (headingCount > 0) {
            headingCount--
        }

        let sublinksClass = determineSublinkClass(links, headingCount)
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label)

        if (links.length > 0) {
            return (
                <li className={panelClassName}>
                    <a href="#d" onClick={this.props.handleClick.bind(null, label)}>
                        <span>{label}</span>
                    </a>
                    <div id={id + "-menu"} className={sublinksClass}>
                        {links}
                        <button type="button" className="close" onClick={this.props.handleClick.bind(null, label)}><span aria-hidden="true">&times;</span></button>
                    </div>
                </li>
            )
        } else {
            return null
        }
    }
});

var LinkFilter = React.createClass({
    render() {
        let activitiesChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('activities') != -1
        let referenceChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('reference') != -1
        let administrationChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('administration') != -1
        return (
            <div id="linkFilter">
                <input onChange={this.props.modifyLinkFilter.bind(null, 'activities')} type="checkbox" id="activities" value="activities" name="linkFilter" checked={activitiesChecked}/><label htmlFor="activities">Activities</label>
                <input onChange={this.props.modifyLinkFilter.bind(null, 'reference')} type="checkbox" id="reference" value="reference" name="linkFilter" checked={referenceChecked}/><label htmlFor="reference">Reference</label>
                <input onChange={this.props.modifyLinkFilter.bind(null, 'administration')} type="checkbox" id="administration" value="administration" name="linkFilter" checked={administrationChecked}/><label htmlFor="administration">Administration</label>
            </div>
        )
    }
});

React.render(
    <Sidebar/>,
    document.getElementById('sidebar')
);

export default Sidebar;