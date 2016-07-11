import React from 'react/addons';
import {validateForm} from '../institutionConfigUtils.js';

let MenuItem = React.createClass({
    contextTypes: {
        updateMenuItem: React.PropTypes.func,
        openUpdateMenuItem: React.PropTypes.func,
        deleteMenuItem: React.PropTypes.func,
        openDeleteMenuItem: React.PropTypes.func
    },
    getInitialState() {
        return {'errors': [], 'errorMessages': []};
    },
    editMenuItem() {
        this.setState({
            'updatedItem': this.props.item,
            'errors': [],
            'errorMessages': []
        });
        if (this.props.editing === this.props.item.get('label')) {
            this.context.openUpdateMenuItem(this.props.item.get(null));
        } else {
            this.context.openUpdateMenuItem(this.props.item.get('label'));
        }
    },
    updateMenuItem() {
        let errorObj = validateForm(this.state.updatedItem.get('label') || '', this.state.updatedItem.get('link') || '');

        if (errorObj.errors.length < 1) {
            this.setState({errors: [], errorMessages: []});
            this.context.updateMenuItem(this.state.updatedItem, this.props.index);
        } else {
            this.setState(errorObj);
        }
    },
    openDeleteMenuItem() {
        if (this.props.deleting === this.props.item.get('label')) {
            this.context.openDeleteMenuItem(this.props.item.get(null));
        } else {
            this.context.openDeleteMenuItem(this.props.item.get('label'));
        }
    },
    deleteMenuItem() {
        this.context.deleteMenuItem(this.props.index);
    },
    updateValue(key, event) {
        let value = $(event.target).val();
        let updatedNewLink = this.state.updatedItem.set(key, value);
        this.setState({'updatedItem': updatedNewLink});
    },
    render() {
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
        if (this.props.editing === this.props.item.get('label')) {
            dialog = (
                <div className="dialog form edit-form">
                    {errorMessage}
                    <div><label>Label:</label></div>
                    <div><input className={labelClass} type="text" value={this.state.updatedItem.get('label')} onChange={this.updateValue.bind(null, 'label')}/></div>
                    <div><label>URL:</label></div>
                    <div><input className={linkClass} type="text" value={this.state.updatedItem.get('link')} onChange={this.updateValue.bind(null, 'link')}/></div>
                    <div>
                        <button className="btn btn-green" onClick={this.updateMenuItem}>Save</button>
                        <button className="btn btn-default" onClick={this.editMenuItem}>Cancel</button>
                    </div>
                </div>
            );
        } else if (this.props.deleting === this.props.item.get('label')) {
            dialog = (
                <div className="dialog form delete-form">
                    <div><label>Are you sure you want to delete {this.props.item.get('label')}?</label></div>
                    <div>
                        <button className="btn btn-red" onClick={this.deleteMenuItem}>Delete</button>
                        <button className="btn btn-default" onClick={this.openDeleteMenuItem}>Cancel</button>
                    </div>
                </div>
            );
        }
        return (
            <li className="item">
                <span className="move"></span>
                <span>{this.props.item.get('label')}</span>
                <div className="actions">
                    <button id="editMenuButton" alt="Edit Menu Item" onClick={this.editMenuItem}><span className="edit"></span></button>
                    <button id="deleteMenuButton" alt="Delete Menu Item" onClick={this.openDeleteMenuItem}><span className="delete"></span></button>
                </div>
                {dialog}
            </li>
        )
    }
});

export default MenuItem;