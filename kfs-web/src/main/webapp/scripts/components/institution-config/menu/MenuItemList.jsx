/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
import Immutable from 'immutable';
import MenuItem from  './MenuItem.jsx';
import {buildGroupSortableDropHandler, validateForm} from '../institutionConfigUtils.js';

export default class MenuItemList extends Component {
    constructor(props) {
        super(props);
        this.state = {'errors': [], 'errorMessages': []};

        this.openAddNewMenuItem = this.openAddNewMenuItem.bind(this);
        this.addNewMenuItem = this.addNewMenuItem.bind(this);
        this.updateValue = this.updateValue.bind(this);
    }

    componentDidMount() {
        let self = this;
        buildGroupSortableDropHandler('item-list', self, 'menu', 'stateUpdateMenu');
    }

    openAddNewMenuItem() {
        this.setState({
            'newItem': new Immutable.Map(),
            'errors': [],
            'errorMessages': []
        });
        if (this.props.addNew) {
            this.props.stateMaintenance.stateOpenAddNewMenuItem(false);
        } else {
            this.props.stateMaintenance.stateOpenAddNewMenuItem(true);
        }
    }

    addNewMenuItem() {
        let errorObj = validateForm(this.state.newItem.get('label') || '', this.state.newItem.get('link') || '');

        if (errorObj.errors.length < 1) {
            this.setState({errors: [], errorMessages: []});
            this.props.stateMaintenance.stateAddNewMenuItem(this.state.newItem);
        } else {
            this.setState(errorObj);
        }
    }

    updateValue(key, event) {
        let value = $(event.target).val();
        let updatedNewItem = this.state.newItem.set(key, value);
        this.setState({'newItem': updatedNewItem});
    }

    render() {
        let items = [];
        for (let i = 0; i < this.props.menu.size; i++) {
            let item = this.props.menu.get(i);
            items.push(
                <MenuItem
                    key={'menu-item-' + i}
                    index={i}
                    item={item}
                    editing={this.props.editing}
                    deleting={this.props.deleting}
                    stateMaintenance={this.props.stateMaintenance}
                />
            );
        }

        let errorMessage;
        if (this.state.errorMessages && this.state.errorMessages.length > 0) {
            let messages = this.state.errorMessages.map(function(message, index) {
                return <li key={index}>{message}</li>
            });
            errorMessage = <ul className="errorMessages">{messages}</ul>;
        }

        let labelClass = this.state.errors.indexOf('label') > -1 ? 'error' : '';
        let linkClass = this.state.errors.indexOf('link') > -1 ? 'error' : '';

        let dialog;
        if (this.props.addNew) {
            dialog = (
                <div className="dialog form add-new-form">
                    {errorMessage}
                    <div><label>Label:</label></div>
                    <div><input className={labelClass} type="text" value={this.state.newItem.get('label')} onChange={this.updateValue.bind(null, 'label')}/></div>
                    <div><label>URL:</label></div>
                    <div><input className={linkClass} type="text" value={this.state.newItem.get('link')} onChange={this.updateValue.bind(null, 'link')}/></div>
                    <div>
                        <button className="btn btn-green" onClick={this.addNewMenuItem}>Save</button>
                        <button className="btn btn-default" onClick={this.openAddNewMenuItem}>Cancel</button>
                    </div>
                </div>
            );
        }

        return (
            <ul id="item-list">
                {items}
                <li className="item new">
                    <div className="add-new-button" onClick={this.openAddNewMenuItem}>
                        <span className="glyphicon glyphicon-plus"></span>Add New
                    </div>
                    {dialog}
                </li>
            </ul>
        )
    }
}
