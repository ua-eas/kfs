import React from 'react';
import { render } from 'react-dom';
import Link from './link.jsx';
import UserPrefs from '../../sys/user_preferences.js';
import KfsUtils from '../../sys/utils.js';

var Sidebar = React.createClass({
    getInitialState() {
        let userPreferences = {};
        userPreferences.checkedLinkFilters = ["activities", "reference", "administration"];
        let sidebarOut = !$('#sidebar').hasClass('collapsed');
        return { principalName: "",
            institutionPreferences: {},
            userPreferences: userPreferences,
            expandedLinkGroup: "",
            expandedSearch: false,
            search: '',
            searchResults: undefined,
            backdoorId: '',
            sidebarOut: sidebarOut
        };
    },
    componentWillMount() {
        let thisComponent = this;
        let found = false;

        UserPrefs.getBackdoorId((backdoorId) => {
            UserPrefs.getPrincipalName(function (principalName) {
                thisComponent.setState({principalName: principalName});
                let preferencesString = localStorage.getItem("institutionPreferences");
                if (preferencesString != null) {
                    let sessionId = KfsUtils.getKualiSessionId();
                    let prefs = JSON.parse(preferencesString);
                    if ((prefs.sessionId == sessionId) && (prefs.principalName == principalName)) {
                        found = true;
                        thisComponent.setState({backdoorId: backdoorId, institutionPreferences: prefs});
                    } else {
                        localStorage.removeItem("institutionPreferences");
                    }
                }

                if (!found) {
                    let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/institution-links/" + principalName;
                    KfsUtils.ajaxCall({
                        url: institutionLinksPath,
                        dataType: 'json',
                        cache: false,
                        type: 'GET',
                        success: function (preferences) {
                            thisComponent.setState({backdoorId: backdoorId, institutionPreferences: preferences});
                            preferences.sessionId = KfsUtils.getKualiSessionId();
                            localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                        }.bind(this),
                        error: function (xhr, status, err) {
                            console.error(status, err.toString());
                        }.bind(this)
                    });
                }

            }, function () {
                console.error("Error retrieving principalName");
            })
        }, (status, message) => {console.error(status, message.toString());});

        UserPrefs.getUserPreferences(function (userPreferences) {
            thisComponent.setState({userPreferences: userPreferences});
        }, function (error) {
            console.log("error getting preferences: " + error);
        });
    },
    componentDidUpdate() {
        let newClass;
        let activePanelId = KfsUtils.buildKeyFromLabel(this.state.expandedLinkGroup) + "-menu";
        let activePanel = $('#' + activePanelId);

        if (!activePanel.parent().hasClass('search') && activePanel.offset()) {
            let panelHeight = activePanel.outerHeight();
            if ($('#sidebar-scroll').outerHeight() < ($('#linkgroups').outerHeight() + $('#sidebar-scroll>div.refresh').outerHeight(true))) {
                let sidebarMiddle = ($('#sidebar-scroll').outerHeight() + 170) / 2;
                let topPosition = sidebarMiddle - (panelHeight / 2);
                activePanel.css('top', topPosition);
                activePanel.addClass('floating');
            } else {
                let activeLink = $('#linkgroups .active');
                let sidebarHeight = $('#sidebar>div').outerHeight();
                let distanceFromTop = activeLink.offset().top - $('#sidebar>div').offset().top;
                let distanceFromBottom = sidebarHeight - distanceFromTop;
                if (distanceFromBottom < distanceFromTop && distanceFromBottom < panelHeight) {
                    if (distanceFromTop > distanceFromBottom && distanceFromBottom > (activeLink.outerHeight() / 2 + panelHeight / 2)) {
                        newClass = 'flowCenter';
                    } else {
                        newClass = 'flowUp';
                    }
                }

                if (newClass) {
                    activePanel.addClass(newClass);
                    if (newClass === 'flowCenter') {
                        activePanel.css('top', (distanceFromTop + activeLink.outerHeight() / 2 - panelHeight / 2));
                    } else if (newClass === 'flowUp') {
                        activePanel.css('bottom', distanceFromBottom - activeLink.outerHeight() + 1);
                    }
                } else {
                    activePanel.css('top', distanceFromTop);
                }
            }
        }
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
    toggleLinkGroup(label, sublinksId) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: ""});
            let sublinks = $('#' + sublinksId);
            sublinks.removeClass('flowUp flowCenter');
            sublinks.css({'bottom': 'inherit', 'top': 'inherit'});
            $('#content-overlay').removeClass('visible');
            $('html').off('click','**');
        } else {
            this.setState({expandedLinkGroup: label});
            $('#content-overlay').addClass('visible');
            let sidebar = this;
            $('html').on('click',function(event) {
                if (!$(event.target).closest('.sublinks').length &&
                    !$(event.target).closest('li.panel.active').length &&
                    !$(event.target).closest('#linkFilter').length) {
                    let sublinks = $('#' + sublinksId);
                    sublinks.removeClass('flowUp flowCenter');
                    sublinks.css({'bottom': 'inherit', 'top': 'inherit'});
                    $('#content-overlay').removeClass('visible');
                    sidebar.setState({expandedLinkGroup: ""});
                }
            });
        }
    },
    toggleSidebar() {
        $('#menu-toggle').toggleClass('rotated');
        $('#sidebar').toggleClass('collapsed');
        $('main.content').toggleClass('fullwidth');
        if (!this.state.sidebarOut) {
            $('li.search>input').focus();
        }
        this.setState({ sidebarOut: !this.state.sidebarOut, expandedLinkGroup: "" });
    },
    refreshLinks() {
        let thisComponent = this;
        let institutionLinksPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/institution-links/" + thisComponent.state.principalName;

        $('.cover').show();
        $('.sidebar-waiting').css('top',($(window).height() / 2) - 20).show();

        KfsUtils.ajaxCall({
            url: institutionLinksPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            beforeSend: function(xhr) {
                xhr.setRequestHeader('cache-control', 'must-revalidate');
            },
            success: function (preferences) {
                thisComponent.setState({institutionPreferences: preferences});
                preferences.sessionId = KfsUtils.getKualiSessionId();
                localStorage.setItem("institutionPreferences", JSON.stringify(preferences));
                $('.cover').hide();
                $('.sidebar-waiting').hide();
            }.bind(this),
            error: function (xhr, status, err) {
                $('#sidebar').removeClass('sidebar-dim');
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    autocompleteSearch(event) {
        let searchString = event.target.value;
        let expandedSearch = searchString.length > 2;

        let newState = {'search': searchString, 'expandedSearch': expandedSearch, expandedLinkGroup: ""};

        if (!expandedSearch) {
            $('#content-overlay').removeClass('visible');
            $('html').off('click','**');
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
                    let filteredLinks = linksOfType.filter(link => {
                        return link.label.toLowerCase().indexOf(lowerSearchString) != -1;
                    }).map(link => {
                        let newLink = $.extend(true, {}, link);
                        let searchPattern = new RegExp('('+searchString+')', 'ig');
                        let splitLabel = newLink.label.split(searchPattern);
                        newLink.label = splitLabel.map(piece => piece.toLowerCase() === lowerSearchString ? <strong>{piece}</strong> : piece);
                        return newLink;
                    });
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
        this.refs.searchBox.getDOMNode().focus();
        this.setState({'search': '', 'searchResults': undefined, 'expandedSearch': false});
        $('#content-overlay').removeClass('visible');
        $('html').off('click','**');
    },
    closeSearch() {
        $('li.search.panel.active div.sublinks.active').removeClass('active');
        $('li.search.panel.active').removeClass('active');
        $('#content-overlay').removeClass('visible');
        $('html').off('click','**');
        this.setState({expandedSearch: false});
    },
    render() {
        let rootPath = KfsUtils.getUrlPathPrefix();
        let linkGroups = [];
        let linkGroupSublinks = [];
        if (this.state.institutionPreferences.linkGroups) {
            let groups = this.state.institutionPreferences.linkGroups;
            for (let i = 0; i < groups.length; i++) {
                linkGroups.push(
                    <LinkGroup key={i}
                        group={groups[i]}
                        handleClick={this.toggleLinkGroup}
                        checkedLinkFilters={this.state.userPreferences.checkedLinkFilters}
                        expandedLinkGroup={this.state.expandedLinkGroup}/>
                );

                linkGroupSublinks.push(
                    <LinkGroupSublinks key={i}
                               group={groups[i]}
                               handleClick={this.toggleLinkGroup}
                               checkedLinkFilters={this.state.userPreferences.checkedLinkFilters}
                               backdoorId={this.state.backdoorId}
                               expandedLinkGroup={this.state.expandedLinkGroup}/>
                );
            }
        }

        let menuToggleClassName = "glyphicon glyphicon-menu-left";
        if (this.state.sidebarOut === false) {
            menuToggleClassName += " rotated";
            $('#sidebar').addClass('collapsed');
        }

        let navSearchClass = "search list-item panel";
        if (this.state.expandedSearch) {
            navSearchClass += " active";
        }

        let searchResultsClass;
        let searchResults = undefined;
        if (this.state.searchResults && Object.keys(this.state.searchResults).length > 0) {
            let finalLinks = [];
            let groupCount = 0;
            for (let resultGroup of Object.keys(this.state.searchResults)) {
                let displayLinks = convertLinks(this.state.searchResults[resultGroup], 'navSearch' + resultGroup, this.state.backdoorId);
                finalLinks = finalLinks.concat(addHeading(displayLinks, resultGroup));
                groupCount++;
            }
            searchResults = finalLinks;

            if (groupCount > 0) {
                groupCount--;
            }

            searchResultsClass = determineSublinkClass(finalLinks, groupCount, this.state.expandedSearch);
        } else if (this.state.expandedSearch) {
            searchResults = [];
            searchResults.push(<div key="no-search-results" className="center">No results found</div>);

            searchResultsClass = "sublinks collapse active";
        }

        if (searchResults) {
            searchResults.push(<button key="close-search-button" type="button" className="close" onClick={this.closeSearch}><span aria-hidden="true">&times;</span></button>);
        }

        return (
            <div>
                <div className="cover"></div>
                <div className="sidebar-waiting"><span className="waiting-icon glyphicon glyphicon-hourglass"></span></div>
                <ul id="filters" className="nav list-group">
                    <li id="home-item">
                        <span id="home"><a href={rootPath}><span className="fa fa-home home-icon"></span>Home</a></span>
                        <span id="menu-toggle" className={menuToggleClassName} onClick={this.toggleSidebar}></span>
                    </li>
                    <li className={navSearchClass}>
                        <input type="search" placeholder="Search" onChange={this.autocompleteSearch} value={this.state.search} ref="searchBox" onFocus={this.autocompleteSearch} />
                        <span className="glyphicon glyphicon-remove remove" onClick={this.clearSearch}></span>
                        <div className={searchResultsClass}>
                            <div className="links-container">
                                {searchResults}
                            </div>
                        </div>
                    </li>
                    <li className="list-item"><LinkFilter checkedLinkFilters={this.state.userPreferences.checkedLinkFilters} modifyLinkFilter={this.modifyLinkFilter} /></li>
                </ul>
                <div id="sidebar-scroll">
                    <ul id="linkgroups" className="nav list-group">
                        {linkGroups}
                    </ul>
                    <div className="refresh">
                        Missing something? <a href="#d" onClick={this.refreshLinks}><span> Refresh Menu </span></a> to make sure your permissions are up to date.
                    </div>
                </div>
                <div>
                    {linkGroupSublinks}
                </div>
            </div>
        )
    }
});


var convertLinks = function(links, type, backdoorId) {
    if (!links) {
        return "";
    }
    let backdoorIdAppender = KfsUtils.buildBackdoorIdAppender(backdoorId);
    return links.map((link, i) => {
        let target = null;
        if ((link.linkType !== 'kfs' && link.linkType !== 'report' ) || link.newTarget) {
            target = '_blank';
        }
        let url = link.linkType === 'rice' ? backdoorIdAppender(link.link) : link.link;
        return <Link key={type + "_" + i} url={url} label={link.label} className="list-group-item" target={target} click={stayOnPage}/>
    })
};

var buildDisplayLinks = function(links, type, checkedLinkFilters, backdoorId) {
    let displayLinks = [];
    if (links && links[type] && checkedLinkFilters && checkedLinkFilters.indexOf(type) != -1) {
        displayLinks = convertLinks(links[type], type, backdoorId);
    }
    return displayLinks;
};

var addHeading = function(links, type) {
    let newLinks = [];
    if (links.length > 0) {
        newLinks = newLinks.concat([<h4 key={type + "_label"}>{type}</h4>]).concat(links);
    }
    return newLinks;
};

var determineSublinkClass = function(links, headingCount, expanded) {
    let sublinksClass = "sublinks collapse";
    if (links.length > (36 - headingCount)) {
        sublinksClass += " col-3";
    } else if (links.length > (18 - headingCount)) {
        sublinksClass += " col-2";
    }
    if (expanded) {
        sublinksClass += " active";
    }
    return sublinksClass;
};

var determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "panel list-item";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName;
};

var LinkGroup = React.createClass({
    render() {
        let label = this.props.group.label;
        let id = KfsUtils.buildKeyFromLabel(label);
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label);

        let links = this.props.group.links;
        let linksCount = 0;
        this.props.checkedLinkFilters.forEach(function(filter) {
            if (links[filter]) {
                linksCount += links[filter].length;
            }
        });

        if (linksCount > 0) {
            return (
                <li className={panelClassName}>
                    <a href="#d" onClick={this.props.handleClick.bind(null, label, id + '-menu')}>
                        <span>{label}</span>
                    </a>
                </li>
            )
        } else {
            return null
        }
    }
});

var LinkGroupSublinks = React.createClass({
    render() {
        let label = this.props.group.label;
        let id = KfsUtils.buildKeyFromLabel(label);

        let activitiesLinks = buildDisplayLinks(this.props.group.links, 'activities', this.props.checkedLinkFilters, this.props.backdoorId);
        let referenceLinks = buildDisplayLinks(this.props.group.links, 'reference', this.props.checkedLinkFilters, this.props.backdoorId);
        let administrationLinks = buildDisplayLinks(this.props.group.links, 'administration', this.props.checkedLinkFilters, this.props.backdoorId);

        let links = addHeading(activitiesLinks, 'Activities');
        links = links.concat(addHeading(referenceLinks, 'Reference'));
        links = links.concat(addHeading(administrationLinks, 'Administration'));

        let headingCount = links.length - (activitiesLinks.length + referenceLinks.length + administrationLinks.length);
        if (headingCount > 0) {
            headingCount--;
        }

        let sublinksClass = determineSublinkClass(links, headingCount, this.props.expandedLinkGroup === label);

        if (links.length > 0) {
            return (
                <div id={id + "-menu"} className={sublinksClass}>
                    <h3>{label}</h3>
                    <div className="links-container">
                        {links}
                    </div>
                    <button type="button" className="close" onClick={this.props.handleClick.bind(null, label, id + '-menu')}><span aria-hidden="true">&times;</span></button>
                </div>
            )
        } else {
            return null
        }
    }
});

var LinkFilter = React.createClass({
    render() {
        let activitiesChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('activities') != -1;
        let referenceChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('reference') != -1;
        let administrationChecked = !this.props.checkedLinkFilters || this.props.checkedLinkFilters.indexOf('administration') != -1;
        return (
            <div id="linkFilter">
                <input onChange={this.props.modifyLinkFilter.bind(null, 'activities')} type="checkbox" id="activities" value="activities" name="linkFilter" checked={activitiesChecked}/><label htmlFor="activities">Activities</label>
                <input onChange={this.props.modifyLinkFilter.bind(null, 'reference')} type="checkbox" id="reference" value="reference" name="linkFilter" checked={referenceChecked}/><label htmlFor="reference">Reference</label>
                <input onChange={this.props.modifyLinkFilter.bind(null, 'administration')} type="checkbox" id="administration" value="administration" name="linkFilter" checked={administrationChecked}/><label htmlFor="administration">Administration</label>
            </div>
        )
    }
});

render(
    <Sidebar/>,
    document.getElementById('sidebar')
);

export default Sidebar;