import React from 'react';
import Immutable from 'immutable';
import MenuItem from  './MenuItem.jsx';
import {buildGroupSortableDropHandler, validateForm} from '../institutionConfigUtils.js';

let MenuItemList = React.createClass({
    contextTypes: {
        updateMenu: React.PropTypes.func,
        addNewMenuItem: React.PropTypes.func,
        openAddNewMenuItem: React.PropTypes.func
    },
    getInitialState() {
        return {'errors': [], 'errorMessages': []};
    },
    componentDidMount() {
        let self = this;
        buildGroupSortableDropHandler('item-list', self, 'menu', 'updateMenu');
    },
    openAddNewMenuItem() {
        this.setState({
            'newItem': new Immutable.Map(),
            'errors': [],
            'errorMessages': []
        });
        if (this.props.addNew) {
            this.context.openAddNewMenuItem(false);
        } else {
            this.context.openAddNewMenuItem(true);
        }
    },
    addNewMenuItem() {
        let errorObj = validateForm(this.state.newItem.get('label') || '', this.state.newItem.get('link') || '');

        if (errorObj.errors.length < 1) {
            this.setState({errors: [], errorMessages: []});
            this.context.addNewMenuItem(this.state.newItem);
        } else {
            this.setState(errorObj);
        }
    },
    updateValue(key, event) {
        let value = $(event.target).val();
        let updatedNewItem = this.state.newItem.set(key, value);
        this.setState({'newItem': updatedNewItem});
    },
    render() {
        let items = [];
        for (let i = 0; i < this.props.menu.size; i++) {
            let item = this.props.menu.get(i);
            items.push(
                <MenuItem key={'menu-item-' + i} index={i} item={item} editing={this.props.editing} deleting={this.props.deleting}/>
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
});

export default MenuItemList;