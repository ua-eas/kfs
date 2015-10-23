import {buildSortableDropHandler} from './institutionconfigutils.js';
import {buildKeyFromLabel} from '../../sys/utils.js';

let LinkGroups = React.createClass({
    contextTypes: {
        updateLinkGroups: React.PropTypes.func,
        addNewLinkGroup: React.PropTypes.func
    },
    componentDidMount() {
        let self = this;
        buildSortableDropHandler('linkGroupsList', self, 'linkGroups', 'updateLinkGroups');
    },
    render() {
        let linkGroupElements = this.props.linkGroups.map((linkGroup, idx) => {
            return <LinkGroup linkGroup={linkGroup}
                              key={buildKeyFromLabel(linkGroup.get('label'))}
                              expandedLinkGroup={this.props.expandedLinkGroup}
                              linkGroupIndex={idx}/>
        });
        return (
            <ul id="linkGroupsList">
                {linkGroupElements}
                <li className="linkgroup new" onClick={this.context.addNewLinkGroup}>
                    <span className="glyphicon glyphicon-plus"></span>Add New
                </li>
            </ul>
        )
    }
});

let LinkGroup = React.createClass({
    contextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func,
        deleteLinkGroup: React.PropTypes.func
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
    deleteGroup(event) {
        event.stopPropagation();
        if (this.props.linkGroup.get('links').size < 1) {
            let confirmed = confirm('Are you sure you want to delete this group?');
            if (confirmed) {
                let index = $(event.target).closest('li').index();
                this.context.deleteLinkGroup(index);
            }
        } else {
            alert('You can only delete empty groups');
        }

    },
    saveLinkGroupName(event) {
        event.stopPropagation();
        let newLabel = $('#groupLabelInput').val();
        let index = $(event.target).closest('li').index();
        this.setState({linkGroupName: newLabel, linkGroupEditing: false});
        this.context.updateLinkGroupName(index, newLabel)
        $('html').off('click');
    },
    updateLinkGroupLabel(event) {
        this.setState({linkGroupName: event.target.value});
    },
    toggleLinkGroup(event) {
        let index = $(event.target).closest('li').index();
        this.context.toggleLinkGroup(index, this.props.linkGroup.get('label'));
    },
    componentDidUpdate(prevProps, prevState) {
        if (this.state.linkGroupEditing && !prevState.linkGroupEditing) {
            let self = this;
            $('html').on('click', function (event) {
                if ($(event.target)[0] !== $('#saveGroupLabelButton')[0] && $(event.target)[0] !== $('#saveGroupLabelButton span')[0] && $(event.target)[0] !== $('#groupLabelInput')[0]) {
                    self.setState({linkGroupName: self.props.linkGroup.get('label'), linkGroupEditing: false});
                }
            });
        } else if (!this.state.linkGroupEditing && prevState.linkGroupEditing) {
            $('html').off('click');
        }
    },
    render() {
        let label = this.state.linkGroupName;
        let panelClassName = determinePanelClassName(this.props.expandedLinkGroup, label);
        let buttons;
        if (this.state.linkGroupEditing) {
            buttons = <div className="actions"><button id="saveGroupLabelButton" alt="Save Link Group Name" onClick={this.saveLinkGroupName}><span className="save">Save</span></button></div>;
        } else {
            buttons =
                <div className="actions">
                    <button id="editGroupLabelButton" alt="Edit Link Group Name" onClick={this.editLabel}><span className="edit"></span></button>
                    <button id="deleteGroupLabelButton" alt="Delete Link Group" onClick={this.deleteGroup}><span className="delete"></span></button>
                </div>;
        }

        return (
            <li className={panelClassName} onClick={this.toggleLinkGroup}>
                <span className="move"></span>
                <LinkGroupLabel label={label} linkGroupEditing={this.state.linkGroupEditing}/>
                {buttons}
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

var determinePanelClassName = function(expandedLinkGroup, label) {
    let panelClassName = "linkgroup";
    if (expandedLinkGroup === label) {
        panelClassName += " active";
    }
    return panelClassName
};

export default LinkGroups;