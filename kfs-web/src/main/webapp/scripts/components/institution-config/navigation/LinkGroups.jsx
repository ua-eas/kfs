import React from 'react';
import {buildGroupSortableDropHandler} from '../institutionConfigUtils.js';
import {buildKeyFromLabel} from '../../../sys/utils.js';


let determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "item";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName
};


let LinkGroups = React.createClass({
    contextTypes: {
        updateLinkGroups: React.PropTypes.func,
        addNewLinkGroup: React.PropTypes.func
    },
    childContextTypes: {
        openDeleteGroup: React.PropTypes.func
    },
    getChildContext() {
        return {
            openDeleteGroup: this.openDeleteGroup
        }
    },
    getInitialState() {
        return {'deleting': null};
    },
    componentDidMount() {
        let self = this;
        buildGroupSortableDropHandler('item-list', self, 'linkGroups', 'updateLinkGroups');
    },
    openDeleteGroup(label) {
        this.setState({'deleting': label});
    },
    render() {
        let linkGroupElements = [];
        for (let idx = 0; idx < this.props.linkGroups.size; idx++) {
            let linkGroup = this.props.linkGroups.get(idx);
            linkGroupElements.push(
                <LinkGroup linkGroup={linkGroup}
                           key={buildKeyFromLabel(linkGroup.get('label'))}
                           deleting={this.state.deleting}
                           expandedLinkGroup={this.props.expandedLinkGroup}
                           linkGroupIndex={idx}/>
            );
        }
        return (
            <ul id="item-list">
                {linkGroupElements}
                <li className="item new" onClick={this.context.addNewLinkGroup}>
                    <div className="add-new-button"><span className="glyphicon glyphicon-plus"></span>Add New</div>
                </li>
            </ul>
        )
    }
});

let LinkGroup = React.createClass({
    contextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func,
        deleteLinkGroup: React.PropTypes.func,
        cancelAddNewLinkGroup: React.PropTypes.func,
        openDeleteGroup: React.PropTypes.func
    },
    childContextTypes: {
        updateLinkGroupLabel: React.PropTypes.func
    },
    getChildContext() {
        return {
            updateLinkGroupLabel: this.updateLinkGroupLabel
        }
    },
    getInitialState() {
        let editing = this.props.linkGroup.get('label') ? false : true;
        return {linkGroupEditing: editing, linkGroupName: this.props.linkGroup.get('label')};
    },
    editLabel(event) {
        event.stopPropagation();
        this.setState({linkGroupEditing: true});
    },
    cancelEditLabel(event) {
        event.stopPropagation();
        if (this.props.linkGroup.get('label')) {
            this.setState({linkGroupName: this.props.linkGroup.get('label'), linkGroupEditing: false});
        } else {
            this.context.cancelAddNewLinkGroup();
        }
    },
    openDeleteGroup(event) {
        event.stopPropagation();
        if (this.props.deleting === this.props.linkGroup.get('label')) {
            this.context.openDeleteGroup(this.props.linkGroup.get(null));
        } else {
            this.context.openDeleteGroup(this.props.linkGroup.get('label'));
        }
    },
    deleteGroup(event) {
        event.stopPropagation();
        let index = $(event.target).closest('li').index();
        this.context.deleteLinkGroup(index);
    },
    saveLinkGroupName(event) {
        event.stopPropagation();
        let newLabel = $('#groupLabelInput').val();
        let index = $(event.target).closest('li').index();
        this.setState({linkGroupName: newLabel, linkGroupEditing: false});
        this.context.updateLinkGroupName(index, newLabel);
    },
    updateLinkGroupLabel(event) {
        this.setState({linkGroupName: event.target.value});
    },
    toggleLinkGroup(event) {
        let index = $(event.target).closest('li').index();
        this.context.toggleLinkGroup(index, this.props.linkGroup.get('label'));
    },
    render() {
        let label = this.state.linkGroupName;
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label);

        let buttons;
        if (this.state.linkGroupEditing) {
            buttons =
                <div className="actions">
                    <button id="cancelGroupLabelButton" alt="Cancel" onClick={this.cancelEditLabel}><span className="cancel">Cancel</span></button>
                    <button id="saveGroupLabelButton" alt="Save Link Group Name" onClick={this.saveLinkGroupName}><span className="save">Save</span></button>
                </div>;
        } else {
            buttons =
                <div className="actions">
                    <button id="editGroupLabelButton" alt="Edit Link Group Name" onClick={this.editLabel}><span className="edit"></span></button>
                    <button id="deleteGroupLabelButton" alt="Delete Link Group" onClick={this.openDeleteGroup}><span className="delete"></span></button>
                </div>;
        }

        let dialog;
        if (this.props.deleting === label) {
            if (this.props.linkGroup.get('links') && this.props.linkGroup.get('links').size < 1) {
                dialog = (
                    <div className="dialog form delete-form">
                        <div><label>Are you sure you want to delete {label}?</label></div>
                        <div>
                            <button className="btn btn-red" onClick={this.deleteGroup}>Delete</button>
                            <button className="btn btn-default" onClick={this.openDeleteGroup}>Cancel</button>
                        </div>
                    </div>
                );
            } else {
                dialog = (
                    <div className="dialog form delete-form">
                        <div><label>You can only delete empty groups.</label></div>
                        <div>
                            <button className="btn btn-default" onClick={this.openDeleteGroup}>OK</button>
                        </div>
                    </div>
                );
            }
        }

        return (
            <li className={panelClassName} onClick={this.toggleLinkGroup}>
                <span className="move"></span>
                <LinkGroupLabel label={label} linkGroupEditing={this.state.linkGroupEditing}/>
                {buttons}
                {dialog}
            </li>
        )
    }
});

let LinkGroupLabel = React.createClass({
    contextTypes: {
        updateLinkGroupLabel: React.PropTypes.func
    },
    editLabelClick(event) {
        event.stopPropagation();
    },
    render() {
        let content = (this.props.linkGroupEditing)
            ? <input id="groupLabelInput" type="text" value={this.props.label} onChange={this.context.updateLinkGroupLabel} onClick={this.editLabelClick}/>
            : <span>{this.props.label}</span>;
        return content
    }
});

export default LinkGroups;