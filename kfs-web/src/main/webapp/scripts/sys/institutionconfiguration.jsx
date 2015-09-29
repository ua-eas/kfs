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
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroups}
                            updateLinkGroups={this.updateLinkGroups}
                            toggleLinkGroup={this.toggleLinkGroup}
                            expandedLinkGroup={this.state.expandedLinkGroup}/>

                <LinkGroupLinks linkGroups={this.state.linkGroups} expandedLinkGroup={this.state.expandedLinkGroup}  updateLinkGroups={this.updateLinkGroups}/>
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
        let linkGroupElements = this.props.linkGroups.map((linkGroup) => {
            return <LinkGroup linkGroup={linkGroup}
                              key={buildKeyFromLabel(linkGroup.get('label'))}
                              handleClick={this.props.toggleLinkGroup}
                              expandedLinkGroup={this.props.expandedLinkGroup}/>
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
    updateSublinkTypeLinks(linkGroups) {
        debugger;
        let self = this;
        let index = this.props.linkGroups.findIndex(function(linkGroup) {
            return linkGroup.get('label') === self.props.groupLabel;
        });
        let updatedLinkGroups = this.props.linkGroups.set(index, this.props.linkGroups.get(index).set('links', linkGroups));
        this.props.updateLinkGroups(updatedLinkGroups);
    },
    render() {
       return (
           <div>
               <h4 key={this.props.type + "Label"}>{this.props.type}</h4>
               <SubLinkTypeLinks links={this.props.links}
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
        let self = this;
        if (this.props.links) {
            let linkElements = this.props.links.filter((link) => {
                return link.get('type') === self.props.type;
            }).map((link) => {
                return <li><span className="list-group-item">{link.get('label')}</span></li>;
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
            return <SubLinkGroup id={id} links={linkGroup.get('links')} groupLabel={linkGroup.get('label')} expandedLinkGroup={this.props.expandedLinkGroup} linkGroups={this.props.linkGroups} updateLinkGroups={this.props.updateLinkGroups}/>
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
    render() {
        let label = this.props.linkGroup.get('label');
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label)

        return (
            <li className={panelClassName}>
                <a href="#d" onClick={this.props.handleClick.bind(null, label)}>
                    <span className="move"></span>
                    <span>{label}</span>
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