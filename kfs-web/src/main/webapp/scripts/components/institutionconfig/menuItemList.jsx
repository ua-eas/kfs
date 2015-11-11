import React from 'react/addons';
import MenuItem from  './menuItem.jsx';
import {buildGroupSortableDropHandler} from './institutionconfigutils.js';

let MenuItemList = React.createClass({
    contextTypes: {
        addNewMenuItem: React.PropTypes.func,
        updateMenu: React.PropTypes.func
    },
    componentDidMount() {
        let self = this;
        buildGroupSortableDropHandler('linkGroupsList', self, 'menu', 'updateMenu');
    },
    render() {
        let items = this.props.menu.map((item, index) => {
            return (
                <MenuItem key={'menu-item-' + index} item={item}/>
            );
        });
        return (
            <ul id="linkGroupsList">
                {items}
                <li className="linkgroup new" onClick={this.props.addNewMenuItem}>
                    <span className="glyphicon glyphicon-plus"></span>Add New
                </li>
            </ul>
        )
    }
});

export default MenuItemList;