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
                this.setState({linkGroups: new Immutable.List(linkGroups)});
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
                              key={buildKeyFromLabel(linkGroup.label)}
                              handleClick={this.props.toggleLinkGroup}
                              expandedLinkGroup={this.props.expandedLinkGroup}/>
        });
        return <ul id="linkGroupsList">{linkGroupElements}</ul>;
    }
});

let SubLinkGroup = React.createClass({
    render() {
        return (
            <div id={this.props.id + "-menu"} className='admin-sublinks collapse'>
                <SubLinkType links={this.props.links} type='activities'/>
                <SubLinkType links={this.props.links} type='reference'/>
                <SubLinkType links={this.props.links} type='administration'/>
            </div>
        )
    }
});

let SubLinkType = React.createClass({
   render() {
       let linkList = "";
       let self = this;
       if (this.props.links) {
           let linkElements = this.props.links.filter((link) => {
               return link.type === self.props.type;
           }).map((link, i) => {
               return <li><Link key={self.props.type + "_" + i} url={link.link} label={link.label}
                                className="list-group-item"/></li>;
           });
           linkList = <ul>{linkElements}</ul>;
       }
       return (
           <div>
               <h4 key={this.props.type + "Label"}>{this.props.type}</h4>
               {linkList}
           </div>
       )
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
        let label = this.props.linkGroup.label
        let id = buildKeyFromLabel(label)

        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label)

        return (
            <li className={panelClassName}>
                <a href="#d" onClick={this.props.handleClick.bind(null, this.props.linkGroup.label)}>
                    <span className="move"></span>
                    <span>{this.props.linkGroup.label}</span>
                </a>
                <SubLinkGroup id={id} links={this.props.linkGroup.links} />
            </li>
        )
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('institutionconfig')
);

export default InstitutionConfig;