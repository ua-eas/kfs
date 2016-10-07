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
import Immutable from 'immutable';
import KfsUtils from '../../../sys/utils.js';
import MenuItemList from './MenuItemList.jsx';

export default class MenuConfig extends Component {
    constructor(props) {
        super(props);

        this.state = {
            menu: new Immutable.List(),
            editing: null,
            deleting: null,
            addNew: false,
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };

        this.stateUpdateMenu = this.stateUpdateMenu.bind(this);
        this.stateOpenUpdateMenuItem = this.stateOpenUpdateMenuItem.bind(this);
        this.stateUpdateMenuItem = this.stateUpdateMenuItem.bind(this);
        this.stateOpenDeleteMenuItem  = this.stateOpenDeleteMenuItem.bind(this);
        this.stateDeleteMenuItem = this.stateDeleteMenuItem.bind(this);
        this.stateOpenAddNewMenuItem = this.stateOpenAddNewMenuItem.bind(this);
        this.stateAddNewMenuItem = this.stateAddNewMenuItem.bind(this);
        this.saveChanges = this.saveChanges.bind(this);
    }

    componentDidMount() {
        let menuPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/menu";
        KfsUtils.ajaxCall({
            url: menuPath,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function(menu) {
                this.setState({
                    menu: Immutable.fromJS(menu)
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    }

    stateUpdateMenu(updatedMenu) {
        let newState = {
            'menu': updatedMenu,
            'hasChanges': true,
            'editing': null,
            'deleting': null,
            'addNew': false
        };
        this.setState(newState);
    }

    stateOpenUpdateMenuItem(label) {
        this.setState({'editing': label, 'deleting': null, 'addNew': false});
    }

    stateUpdateMenuItem(updatedItem, index) {
        let updatedMenu = this.state.menu.set(index, updatedItem);
        this.stateUpdateMenu(updatedMenu);
    }

    stateOpenDeleteMenuItem(label) {
        this.setState({'editing': null, 'deleting': label, 'addNew': false});
    }

    stateDeleteMenuItem(index) {
        let updatedMenu = this.state.menu.delete(index);
        this.stateUpdateMenu(updatedMenu);
    }

    stateOpenAddNewMenuItem(open) {
        this.setState({'editing': null, 'deleting': null, 'addNew': open});
    }

    stateAddNewMenuItem(newItem) {
        let updatedMenu = this.state.menu.push(newItem);
        this.stateUpdateMenu(updatedMenu);
    }

    saveChanges() {
        let menuPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/menu";
        KfsUtils.ajaxCall({
            url: menuPath,
            dataType: 'json',
            contentType: 'application/json',
            cache: false,
            type: 'PUT',
            data: JSON.stringify(this.state.menu),
            success: function() {
                let spanStyle = {
                    color: '#6DA487'
                };
                this.setState({
                    hasChanges: false,
                    saveButtonText: <span style={spanStyle}><span className="glyphicon glyphicon-ok"></span>SAVED</span>
                });
                $.notify('Save Successful!', 'success');
            }.bind(this),
            error: function(xhr, status, err) {
                let message = xhr.responseText ? 'Save failed: ' + xhr.responseText : 'Save failed.';
                $.notify(message, 'error');
                console.error(status, err.toString());
            }.bind(this)
        });
    }

    render() {
        let stateMaintenance = {
            stateAddNewMenuItem: this.stateAddNewMenuItem,
            stateUpdateMenu: this.stateUpdateMenu,
            stateUpdateMenuItem: this.stateUpdateMenuItem,
            stateOpenUpdateMenuItem: this.stateOpenUpdateMenuItem,
            stateDeleteMenuItem: this.stateDeleteMenuItem,
            stateOpenDeleteMenuItem: this.stateOpenDeleteMenuItem,
            stateOpenAddNewMenuItem: this.stateOpenAddNewMenuItem
        };
        let saveDisabled;
        let saveButtonClass = 'btn btn-green';
        let saveButtonText = this.state.saveButtonText;
        if (!this.state.hasChanges) {
            saveDisabled = true;
            saveButtonClass += ' disabled';
        } else {
            saveButtonText = "SAVE CHANGES";
        }

        let mainClassName = 'menu-config main';
        if (this.state.addNew || this.state.editing) {
            mainClassName += ' add-new';
        }
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span className="glyphicon glyphicon-cog"></span>Menu Configuration</h1>
                </div>

                <div className={mainClassName}>
                    <MenuItemList
                        menu={this.state.menu}
                        editing={this.state.editing}
                        deleting={this.state.deleting}
                        addNew={this.state.addNew}
                        stateMaintenance={stateMaintenance}
                    />
                </div>

                <div className="buttonbar">
                    <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                </div>
            </div>
        )
    }
}
