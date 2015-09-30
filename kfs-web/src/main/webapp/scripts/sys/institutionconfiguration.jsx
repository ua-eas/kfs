import Link from '../link.jsx';
import {getUrlPathPrefix, buildKeyFromLabel} from './utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroups: new Immutable.List(), expandedLinkGroup: ""};
    },
    componentWillMount() {
        let linkGroupPath = getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroups: Immutable.fromJS(linkGroups)});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    toggleLinkGroup(label) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: ""});
        } else {
            this.setState({expandedLinkGroup: label});
        }
    },
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: linkGroups});
    },
    updateLinkGroupName(linkGroupIndex, newName) {
        let linkGroup = this.state.linkGroups.get(linkGroupIndex);
        let updatedLinkGroup = linkGroup.set('label', newName);
        let updatedLinkGroups = this.state.linkGroups.set(linkGroupIndex, updatedLinkGroup);
        this.setState({linkGroups: updatedLinkGroups});
    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroups}
                            updateLinkGroups={this.updateLinkGroups}
                            toggleLinkGroup={this.toggleLinkGroup}
                            expandedLinkGroup={this.state.expandedLinkGroup}
                            linkGroupNameUpdater={this.updateLinkGroupName}/>

                <LinkGroupLinks linkGroups={this.state.linkGroups}
                                expandedLinkGroup={this.state.expandedLinkGroup}
                                updateLinkGroups={this.updateLinkGroups}/>
            </div>
        )
    }
});

let moveElement = function(list, fromIndex, toIndex) {
    let movingElement = list.get(fromIndex);
    let updatedList = list.delete(fromIndex).splice(toIndex, 0, movingElement);
    return updatedList;
};

let buildSortableDropHandler = function(elementId, component, sortableElementsPropertyName, updatingFunctionPropertyName) {
    let ele = $("#"+elementId);
    if (ele) {
        ele.sortable({
            start: function (event, ui) {
                $(ui.item).data("startindex", ui.item.index());
            },
            update: function (event, ui) {
                let startIndex = ui.item.data("startindex");
                let newIndex = ui.item.index();
                if (newIndex != startIndex) {
                    let updatedLinkGroups = moveElement(component.props[sortableElementsPropertyName], startIndex, newIndex);
                    $("#"+elementId).sortable('cancel');
                    component.props[updatingFunctionPropertyName](updatedLinkGroups);
                }
            }
        });
        ele.disableSelection();
    }
}

let LinkGroups = React.createClass({
    componentDidMount() {
        let self = this;
        buildSortableDropHandler('linkGroupsList', self, 'linkGroups', 'updateLinkGroups');
    },
    render() {
        let linkGroupElements = this.props.linkGroups.map((linkGroup, idx) => {
            return <LinkGroup linkGroup={linkGroup}
                              key={buildKeyFromLabel(linkGroup.get('label'))}
                              handleClick={this.props.toggleLinkGroup}
                              expandedLinkGroup={this.props.expandedLinkGroup}
                              linkGroupIndex={idx}
                              linkGroupNameUpdater={this.props.linkGroupNameUpdater}/>
        });
        return <ul id="linkGroupsList">{linkGroupElements}</ul>;
    }
});

let SubLinkGroup = React.createClass({
    render() {
        let divClassName = 'admin-sublinks';
        if (this.props.groupLabel !== this.props.expandedLinkGroup) {
            divClassName += ' collapse';
        }
        return (
            <div id={this.props.id + "-menu"} className={divClassName}>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='activities' linkGroups={this.props.linkGroups} updateLinkGroups={this.props.updateLinkGroups}/>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='reference' linkGroups={this.props.linkGroups} updateLinkGroups={this.props.updateLinkGroups}/>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='administration' linkGroups={this.props.linkGroups} updateLinkGroups={this.props.updateLinkGroups}/>
            </div>
        )
    }
});

let SubLinkType = React.createClass({
    updateSublinkTypeLinks(links) {
        let self = this;
        let index = this.props.linkGroups.findIndex(function(linkGroup) {
            return linkGroup.get('label') === self.props.groupLabel;
        });
        let linksWithoutCurrentType = this.props.linkGroups.get(index).get('links').filter((link) => {
            return link.get('type') !== self.props.type;
        });
        let updatedLinks = links.concat(linksWithoutCurrentType);
        let updatedLinkGroups = this.props.linkGroups.set(index, this.props.linkGroups.get(index).set('links', updatedLinks));
        this.props.updateLinkGroups(updatedLinkGroups);
    },
    render() {
        let self = this;
        let linksForType = this.props.links.filter((link) => {
            return link.get('type') === self.props.type;
        });
       return (
           <div>
               <h4>{this.props.type}</h4>
               <SubLinkTypeLinks links={linksForType}
                                 type={this.props.type}
                                 groupLabel={this.props.groupLabel}
                                 updateSublinkTypeLinks={this.updateSublinkTypeLinks}/>
           </div>
       )
   }
});

let SubLinkTypeLinks = React.createClass({
    componentDidMount() {
        let self = this;
        let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        buildSortableDropHandler(id, self, 'links', 'updateSublinkTypeLinks');
    },
    render() {
        let linkList = "";
        if (this.props.links) {
            let linkElements = this.props.links.map((link, idx) => {
                return <li key={idx}><span className="list-group-item"><span className="move"></span>{link.get('label')}</span></li>;
            });
            let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
            linkList = <ul id={id}>{linkElements}</ul>;
        }
        return linkList
    }
});

let LinkGroupLinks = React.createClass({
    render() {
        let linkGroupLinkElements = this.props.linkGroups.map((linkGroup) => {
            let id = buildKeyFromLabel(linkGroup.get('label'))
            return <SubLinkGroup key={'subLinkGroup-' + id} id={id} links={linkGroup.get('links')} groupLabel={linkGroup.get('label')} expandedLinkGroup={this.props.expandedLinkGroup} linkGroups={this.props.linkGroups} updateLinkGroups={this.props.updateLinkGroups}/>
        });
        return <div id="linkGroupLinksList">{linkGroupLinkElements}</div>;
    }
});

var determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "linkgroup"
    if (expandedLinkGroup === label) {
        panelClassName += " active"
    }
    return panelClassName
}

let LinkGroup = React.createClass({
    getInitialState() {
        return {linkGroupEditing: false, linkGroupName: this.props.linkGroup.get('label')};
    },
    editLabel(event) {
        event.stopPropagation();
        this.setState({linkGroupEditing: true});
    },
    saveLinkGroupName(event) {
        debugger;
        event.stopPropagation();
        let newLabel = $(event.target).parent().prev().val();
        let index = $(event.target).parent().parent().index();
        this.setState({linkGroupName: newLabel});
        this.setState({linkGroupEditing: false});
        this.props.linkGroupNameUpdater(index, newLabel)
    },
    updateLinkGroupLabel(event) {
        this.setState({linkGroupName: event.target.value});
    },
    cancelLinkGroupLabelChanges(event) {
        event.stopPropagation();
        this.setState({linkGroupName: this.props.linkGroup.get('label')});
        this.setState({linkGroupEditing: false});
    },
    render() {
        let label = this.state.linkGroupName;
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label);
        let editButton = (this.state.linkGroupEditing)
            ? <img src="../../static/images/save.png" alt="Save Link Group Name Changes" onClick={this.saveLinkGroupName}/>
            : <img src="../../static/images/edit-link-group.png" alt="Edit Link Group Name" onClick={this.editLabel}/>;

        return (
            <li className={panelClassName} onClick={this.props.handleClick.bind(null, label)}>
                <span className="move"></span>
                <LinkGroupLabel label={label}
                                linkGroupEditing={this.state.linkGroupEditing}
                                updateLinkGroupLabel={this.updateLinkGroupLabel}
                                cancelLinkGroupLabelChanges={this.cancelLinkGroupLabelChanges}/>
                <div className="actions">{editButton}</div>
            </li>
        )
    }
});

let LinkGroupLabel = React.createClass({
    editLabelClick(event) {
        event.stopPropagation();
    },
    render() {
       let content = (this.props.linkGroupEditing)
           ? <input type="text" value={this.props.label} onChange={this.props.updateLinkGroupLabel} onBlur={this.props.cancelLinkGroupLabelChanges} onClick={this.editLabelClick}/>
           : <span>{this.props.label}</span>;
       return content
   }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('institutionconfig')
);

export default InstitutionConfig;