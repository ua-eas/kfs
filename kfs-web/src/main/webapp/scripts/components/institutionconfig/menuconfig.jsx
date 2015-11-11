import React from 'react/addons';
import Immutable from 'immutable';
import KfsUtils from '../../sys/utils.js';
import MenuItemList from './menuItemList.jsx';

let MenuConfig = React.createClass({
    childContextTypes: {
        addNewMenuItem: React.PropTypes.func,
        updateMenu: React.PropTypes.func
    },
    getChildContext() {
        return {
            addNewMenuItem: this.addNewMenuItem,
            updateMenu: this.updateMenu
        }
    },
    getInitialState() {
        return {
            menu: new Immutable.List(),
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };
    },
    componentWillMount() {
        let menuPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/menu";
        $.ajax({
            url: menuPath,
            dataType: 'json',
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
    addNewMenuItem() {
        console.log('Add New Menu Item clicked');
    },
    updateMenu(updatedMenu) {
        let newState = {'menu': updatedMenu, 'hasChanges': true};
        this.setState(newState)
    },
    saveChanges() {
        console.log('Save Changes clicked');
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
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span className="glyphicon glyphicon-cog"></span>Menu Configuration</h1>
                </div>

                <div className="menuconfig">
                    <MenuItemList menu={this.state.menu} updateMenu={this.updateMenu} addNewMenuItem={this.addNewMenuItem}/>
                </div>

                <div className="buttonbar">
                    <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                </div>
            </div>
        )
    }
});

export default MenuConfig;