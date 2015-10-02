import {buildSortableDropHandler} from './institutionconfigutils.js';
import {buildKeyFromLabel} from '../../sys/utils.js';

let LinkGroups = React.createClass({
    componentDidMount() {
        let self = this;
        buildSortableDropHandler('linkGroupsList', self, 'linkGroups', 'updateLinkGroups');
    },
    contextTypes: {
        updateLinkGroups: React.PropTypes.func
    },
    render() {
        let linkGroupElements = this.props.linkGroups.map((linkGroup, idx) => {
            return <LinkGroup linkGroup={linkGroup}
                              key={buildKeyFromLabel(linkGroup.get('label'))}
                              expandedLinkGroup={this.props.expandedLinkGroup}
                              linkGroupIndex={idx}/>
        });
        return <ul id="linkGroupsList">{linkGroupElements}</ul>;
    }
});

let LinkGroup = React.createClass({
    contextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func
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
        return {linkGroupEditing: false, linkGroupName: this.props.linkGroup.get('label')};
    },
    editLabel(event) {
        event.stopPropagation();
        this.setState({linkGroupEditing: true});
    },
    saveLinkGroupName(event) {
        event.stopPropagation();
        let newLabel = $(event.target).parent().prev().val();
        let index = $(event.target).parent().parent().index();
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
                if ($(event.target)[0] !== $('#saveGroupLabelButton')[0] && $(event.target)[0] !== $('#groupLabelInput')[0]) {
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
        let editButton = (this.state.linkGroupEditing)
            ? <button id="saveGroupLabelButton" alt="Save Link Group Name Changes" onClick={this.saveLinkGroupName}>Save</button>
            : <button id="editGroupLabelButton" alt="Edit Link Group Name" onClick={this.editLabel}><span className="edit"></span></button>;

        return (
            <li className={panelClassName} onClick={this.toggleLinkGroup}>
                <span className="move"></span>
                <LinkGroupLabel label={label}
                                linkGroupEditing={this.state.linkGroupEditing}/>
                <div className="actions">{editButton}</div>
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