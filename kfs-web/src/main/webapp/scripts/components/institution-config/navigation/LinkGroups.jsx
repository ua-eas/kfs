/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import React, {Component} from 'react';
import {buildGroupSortableDropHandler} from '../institutionConfigUtils.js';
import {buildKeyFromLabel} from '../../../sys/utils.js';


let determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "item";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName
};


export default class LinkGroups extends Component {
    constructor(props) {
        super(props);
        this.state = {'deleting': null};

        this.stateOpenDeleteGroup = this.stateOpenDeleteGroup.bind(this);
    }

    componentDidMount() {
        let self = this;
        buildGroupSortableDropHandler('item-list', self, 'linkGroups', 'stateUpdateLinkGroups');
    }

    stateOpenDeleteGroup(label) {
        this.setState({'deleting': label});
    }

    render() {
        let stateMaintenance = this.props.stateMaintenance;
        stateMaintenance["stateOpenDeleteGroup"] = this.stateOpenDeleteGroup;

        let linkGroupElements = [];
        for (let idx = 0; idx < this.props.linkGroups.size; idx++) {
            let linkGroup = this.props.linkGroups.get(idx);
            linkGroupElements.push(
                <LinkGroup linkGroup={linkGroup}
                           key={buildKeyFromLabel(linkGroup.get('label'))}
                           deleting={this.state.deleting}
                           expandedLinkGroup={this.props.expandedLinkGroup}
                           linkGroupIndex={idx}
                           stateMaintenance={stateMaintenance}/>
            );
        }
        return (
            <ul id="item-list">
                {linkGroupElements}
                <li className="item new" onClick={stateMaintenance.stateAddNewLinkGroup}>
                    <div className="add-new-button"><span className="glyphicon glyphicon-plus"></span>Add New</div>
                </li>
            </ul>
        )
    }
}

class LinkGroup extends Component {
    constructor(props) {
        super(props);
        let editing = this.props.linkGroup.get('label') ? false : true;
        this.state = {linkGroupEditing: editing, linkGroupName: this.props.linkGroup.get('label')};

        this.editLabel = this.editLabel.bind(this);
        this.cancelEditLabel = this.cancelEditLabel.bind(this);
        this.openDeleteGroup = this.openDeleteGroup.bind(this);
        this.deleteGroup = this.deleteGroup.bind(this);
        this.saveLinkGroupName = this.saveLinkGroupName.bind(this);
        this.stateUpdateLinkGroupLabel = this.stateUpdateLinkGroupLabel.bind(this);
        this.toggleLinkGroup = this.toggleLinkGroup.bind(this);
    }

    editLabel(event) {
        event.stopPropagation();
        this.setState({linkGroupEditing: true});
    }

    cancelEditLabel(event) {
        event.stopPropagation();
        if (this.props.linkGroup.get('label')) {
            this.setState({linkGroupName: this.props.linkGroup.get('label'), linkGroupEditing: false});
        } else {
            this.props.stateMaintenance.stateCancelAddNewLinkGroup();
        }
    }

    openDeleteGroup(event) {
        event.stopPropagation();
        if (this.props.deleting === this.props.linkGroup.get('label')) {
            this.props.stateMaintenance.stateOpenDeleteGroup(this.props.linkGroup.get(null));
        } else {
            this.props.stateMaintenance.stateOpenDeleteGroup(this.props.linkGroup.get('label'));
        }
    }

    deleteGroup(event) {
        event.stopPropagation();
        let index = $(event.target).closest('li').index();
        this.props.stateMaintenance.stateDeleteLinkGroup(index);
    }

    saveLinkGroupName(event) {
        event.stopPropagation();
        let newLabel = $('#groupLabelInput').val();
        let index = $(event.target).closest('li').index();
        this.setState({linkGroupName: newLabel, linkGroupEditing: false});
        this.props.stateMaintenance.stateUpdateLinkGroupName(index, newLabel);
    }

    stateUpdateLinkGroupLabel(event) {
        this.setState({linkGroupName: event.target.value});
    }

    toggleLinkGroup(event) {
        let index = $(event.target).closest('li').index();
        this.props.stateMaintenance.stateToggleLinkGroup(index, this.props.linkGroup.get('label'));
    }

    render() {
        let stateMaintenance = this.props.stateMaintenance;
        stateMaintenance["stateUpdateLinkGroupLabel"] = this.stateUpdateLinkGroupLabel;

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
        	let canDelete = true;
        	let linksToCheck = this.props.linkGroup.get('links');
        	if (linksToCheck) {
        		if (linksToCheck.get('activities') && linksToCheck.get('activities').size > 0
        		 || linksToCheck.get('reference') && linksToCheck.get('reference').size > 0
        		 || linksToCheck.get('administration') && linksToCheck.get('administration').size > 0) {
        		 	canDelete = false;
        		}
        	}
            if (canDelete) {
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
                <LinkGroupLabel stateMaintenance={stateMaintenance} label={label} linkGroupEditing={this.state.linkGroupEditing}/>
                {buttons}
                {dialog}
            </li>
        )
    }
}

class LinkGroupLabel extends Component {
    constructor(props) {
        super(props);
        this.editLabelClick = this.editLabelClick.bind(this);
    }

    editLabelClick(event) {
        event.stopPropagation();
    }

    render() {
        let content = (this.props.linkGroupEditing)
            ? <input id="groupLabelInput" type="text" value={this.props.label} onChange={this.props.stateMaintenance.stateUpdateLinkGroupLabel} onClick={this.editLabelClick}/>
            : <span>{this.props.label}</span>;
        return content
    }
}
