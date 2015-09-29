import Link from '../link.jsx';
import KfsUtils from './utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroups: new Immutable.List(), expandedLinkGroup: ""};
    },
    componentWillMount() {
        let linkGroupPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroups: new Immutable.List(linkGroups)});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
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
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: linkGroups});

    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroups}
                            updateLinkGroups={this.updateLinkGroups}
                            toggleLinkGroup={this.toggleLinkGroup}
                            expandedLinkGroup={this.state.expandedLinkGroup}/>
            </div>
        )
    }
});

let LinkGroups = React.createClass({
    removeElementAt(list, removeIndex) {
        if (removeIndex === 0) {
            return list.slice(1);
        } else if (list.size === (removeIndex + 1)) {
            return list.slice(0, -1);
        } else {
            return list.slice(0, removeIndex).concat(list.slice(removeIndex+1));
        }
    },
    insertElementAt(list, ele, addIndex) {
        let eleList = Immutable.List.of(ele);
        if (addIndex === 0) {
            return eleList.concat(list);
        } else if (list.size === (addIndex + 1)) {
            return list.concat(eleList);
        } else {
            return list.slice(0, addIndex).concat(eleList).concat(list.slice(addIndex));
        }
    },
    moveElement(list, fromIndex, toIndex) {
        let movingElement = list.get(fromIndex)
        let updatedList = this.insertElementAt(this.removeElementAt(list, fromIndex), movingElement, toIndex);
        return updatedList;
    },
    componentDidMount() {
        let self = this;

        let ele = $("#sortable");
        if (ele) {
            ele.sortable({
                start: function (event, ui) {
                    $(ui.item).data("startindex", ui.item.index());
                },
                update: function (event, ui) {
                    event.stopPropagation();
                    let startIndex = ui.item.data("startindex");
                    let newIndex = ui.item.index();
                    if (newIndex != startIndex) {
                        let updatedLinkGroups = self.moveElement(self.props.linkGroups, startIndex, newIndex);
                        $("#sortable").sortable('cancel');
                        self.props.updateLinkGroups(updatedLinkGroups);
                    }
                }
            });
            ele.disableSelection();
        }
    },
    render() {
        let linkGroupElements = this.props.linkGroups.map((linkGroup) => {
            return <LinkGroup linkGroup={linkGroup}
                              key={KfsUtils.buildKeyFromLabel(linkGroup.label)}
                              handleClick={this.props.toggleLinkGroup}
                              expandedLinkGroup={this.props.expandedLinkGroup}/>
        });
        return <ul id="sortable">{linkGroupElements}</ul>;
    }
});

var filterLinks = function(links, type) {
    return links.filter(function(link) {
        return link.type === type
    }).map((link, i) => {
        return <Link key={type + "_" + i} url={link.link} label={link.label} className="list-group-item"/>
    })
}

var addHeading = function(links, type) {
    return ([<h4 key={type + "Label"}>{type}</h4>]).concat(links);
}

var determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "linkgroup"
    if (expandedLinkGroup === label) {
        panelClassName += " active"
    }
    return panelClassName
}

let LinkGroup = React.createClass({
    render() {
        let label = this.props.linkGroup.label
        let id = KfsUtils.buildKeyFromLabel(label)

        let activitiesLinks = filterLinks(this.props.linkGroup.links, 'activities');
        let referenceLinks = filterLinks(this.props.linkGroup.links, 'reference');
        let administrationLinks = filterLinks(this.props.linkGroup.links, 'administration');

        let links = addHeading(activitiesLinks, 'Activities')
        links = links.concat(addHeading(referenceLinks, 'Reference'))
        links = links.concat(addHeading(administrationLinks, 'Administration'))

        let sublinksClass = "sublinks collapse"
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label)

        return (
            <li className={panelClassName}>
                <a href="#d" onClick={this.props.handleClick.bind(null, this.props.linkGroup.label)}>
                    <span className="move"></span>
                    <span>{this.props.linkGroup.label}</span>
                    <div id={id + "-menu"} className={sublinksClass}>
                        {links}
                        <button type="button" className="close" onClick={this.props.handleClick.bind(null, this.props.linkGroup.label)}><span aria-hidden="true">&times;</span></button>
                    </div>
                </a>
            </li>
        )
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('institutionconfig')
);

export default InstitutionConfig;