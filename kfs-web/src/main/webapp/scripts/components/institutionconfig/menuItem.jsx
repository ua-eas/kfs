import React from 'react/addons';
import {buildGroupSortableDropHandler} from './institutionconfigutils.js';

let MenuItem = React.createClass({
    getInitialState() {
        return {editMode: false};
    },
    editMenuItem() {
        let newItem = this.props.item;
        this.setState({'editMode': !this.state.editMode, 'newItem': newItem});
    },
    updateMenuItem() {

    },
    deleteMenuItem() {

    },
    updateValue(key, event) {
        let value = $(event.target).val();
        let updatedNewLink = this.state.newItem.set(key, value);
        this.setState({'newItem': updatedNewLink});
    },
    render() {
        let dialog;
        if (this.state.editMode) {
            dialog = (
                <div className="dialog form">
                    <div><label>Label:</label></div>
                    <div><input type="text" value={this.state.newItem.get('label')} onChange={this.updateValue.bind(null, 'label')}/></div>
                    <div><label>URL:</label></div>
                    <div><input type="text" value={this.state.newItem.get('link')} onChange={this.updateValue.bind(null, 'link')}/></div>
                    <div>
                        <button className="btn btn-green" onClick={this.updateMenuItem}>Save</button>
                        <button className="btn btn-default" onClick={this.editMenuItem}>Cancel</button>
                    </div>
                </div>
            );
        }
        return (
            <li className="linkgroup">
                <span className="move"></span>
                <span>{this.props.item.get('label')}</span>
                <div className="actions">
                    <button id="editMenuButton" alt="Edit Menu Item" onClick={this.editMenuItem}><span className="edit"></span></button>
                    <button id="deleteMenuButton" alt="Delete Menu Item" onClick={this.deleteMenuItem}><span className="delete"></span></button>
                </div>
                {dialog}
            </li>
        )
    }
});

export default MenuItem;