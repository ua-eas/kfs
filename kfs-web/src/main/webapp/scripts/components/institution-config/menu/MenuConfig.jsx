import React from 'react';
import Immutable from 'immutable';
import KfsUtils from '../../../sys/utils.js';
import MenuItemList from './MenuItemList.jsx';

let MenuConfig = React.createClass({
    childContextTypes: {
        addNewMenuItem: React.PropTypes.func,
        updateMenu: React.PropTypes.func,
        updateMenuItem: React.PropTypes.func,
        openUpdateMenuItem: React.PropTypes.func,
        deleteMenuItem: React.PropTypes.func,
        openDeleteMenuItem: React.PropTypes.func,
        openAddNewMenuItem: React.PropTypes.func
    },
    getChildContext() {
        return {
            addNewMenuItem: this.addNewMenuItem,
            updateMenu: this.updateMenu,
            updateMenuItem: this.updateMenuItem,
            openUpdateMenuItem: this.openUpdateMenuItem,
            deleteMenuItem: this.deleteMenuItem,
            openDeleteMenuItem: this.openDeleteMenuItem,
            openAddNewMenuItem: this.openAddNewMenuItem
        }
    },
    getInitialState() {
        return {
            menu: new Immutable.List(),
            editing: null,
            deleting: null,
            addNew: false,
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };
    },
    componentWillMount() {
        let menuPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/menu";
        $.ajax({
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
    },
    updateMenu(updatedMenu) {
        let newState = {
            'menu': updatedMenu,
            'hasChanges': true,
            'editing': null,
            'deleting': null,
            'addNew': false
        };
        this.setState(newState);
    },
    openUpdateMenuItem(label) {
        this.setState({'editing': label, 'deleting': null, 'addNew': false});
    },
    updateMenuItem(updatedItem, index) {
        let updatedMenu = this.state.menu.set(index, updatedItem);
        this.updateMenu(updatedMenu);
    },
    openDeleteMenuItem(label) {
        this.setState({'editing': null, 'deleting': label, 'addNew': false});
    },
    deleteMenuItem(index) {
        let updatedMenu = this.state.menu.delete(index);
        this.updateMenu(updatedMenu);
    },
    openAddNewMenuItem(open) {
        this.setState({'editing': null, 'deleting': null, 'addNew': open});
    },
    addNewMenuItem(newItem) {
        let updatedMenu = this.state.menu.push(newItem);
        this.updateMenu(updatedMenu);
    },
    saveChanges() {
        let menuPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/menu";
        $.ajax({
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
    },
    render() {
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
        if (this.state.addNew) {
            mainClassName += ' add-new';
        }
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span className="glyphicon glyphicon-cog"></span>Menu Configuration</h1>
                </div>

                <div className={mainClassName}>
                    <MenuItemList menu={this.state.menu}
                                  editing={this.state.editing}
                                  deleting={this.state.deleting}
                                  addNew={this.state.addNew}/>
                </div>

                <div className="buttonbar">
                    <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                </div>
            </div>
        )
    }
});

export default MenuConfig;