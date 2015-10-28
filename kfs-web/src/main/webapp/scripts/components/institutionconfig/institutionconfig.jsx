import LinkGroups from './linkgroups.jsx';
import LinkGroupLinks from './linkgrouplinks.jsx';
import {getUrlPathPrefix} from '../../sys/utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let InstitutionConfig = React.createClass({
    childContextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroups: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func,
        addNewLinkGroup: React.PropTypes.func,
        deleteLinkGroup: React.PropTypes.func,
        addNewCustomLink: React.PropTypes.func,
        updateExistingCustomLink: React.PropTypes.func,
        deleteExistingCustomLink: React.PropTypes.func
    },
    getChildContext() {
        return {
            toggleLinkGroup: this.toggleLinkGroup,
            updateLinkGroups: this.updateLinkGroups,
            updateLinkGroupName: this.updateLinkGroupName,
            addNewLinkGroup: this.addNewLinkGroup,
            deleteLinkGroup: this.deleteLinkGroup,
            addNewCustomLink: this.addNewCustomLink,
            updateExistingCustomLink: this.updateExistingCustomLink,
            deleteExistingCustomLink: this.deleteExistingCustomLink
        }
    },
    getInitialState() {
        return {
            linkGroups: new Immutable.List(),
            expandedLinkGroup: undefined,
            topGroupSelected: false,
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };
    },
    componentWillMount() {
        let linkGroupPath = getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(preferences) {
                this.setState({
                    linkGroups: Immutable.fromJS(preferences.linkGroups),
                    institutionId: preferences.institutionId
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    toggleLinkGroup(index, label) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: undefined, topGroupSelected: false});
        } else if (label) {
            let topGroupSelected = false;
            if (index === 0) {
                topGroupSelected = true;
            }
            this.setState({expandedLinkGroup: label, topGroupSelected: topGroupSelected});
        }
    },
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: linkGroups, hasChanges: true});
    },
    updateLinkGroupName(linkGroupIndex, newName) {
        let linkGroup = this.state.linkGroups.get(linkGroupIndex);
        let updatedLinkGroup = linkGroup.set('label', newName);
        let updatedLinkGroups = this.state.linkGroups.set(linkGroupIndex, updatedLinkGroup);
        this.setState({linkGroups: updatedLinkGroups, hasChanges: true});
    },
    addNewLinkGroup() {
        let linkGroups = this.state.linkGroups;
        let newLinkGroup = Immutable.fromJS({label: '', links: []});
        let updatedLinkGroups = linkGroups.push(newLinkGroup);
        this.setState({linkGroups: updatedLinkGroups})
    },
    deleteLinkGroup(index) {
        let linkGroups = this.state.linkGroups;
        let updatedLinkGroups = linkGroups.splice(index, 1);
        this.setState({linkGroups: updatedLinkGroups, hasChanges: true});
    },
    addNewCustomLink(groupIndex, newLink) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let links = linkGroup.get('links');
        let updatedLinks = links.push(newLink);
        let updatedLinkGroup = linkGroup.set('links', updatedLinks);
        let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
        this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
    },
    updateExistingCustomLink(groupIndex, oldLink, updatedLink) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let links = linkGroup.get('links');
        let index = -1;

        let jsLinks = links.toJS();
        let jsOldLink = oldLink.toJS();
        _.each(jsLinks, function(data, idx) {
            if (_.isEqual(data, jsOldLink)) {
                index = idx;
                return;
            }
        });
        let updatedLinks = links.set(index, updatedLink);
        let updatedLinkGroup = linkGroup.set('links', updatedLinks);
        let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
        this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
    },
    deleteExistingCustomLink(groupIndex, oldLink) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let links = linkGroup.get('links');
        let index = -1;

        let jsLinks = links.toJS();
        let jsOldLink = oldLink.toJS();
        _.each(jsLinks, function(data, idx) {
            if (_.isEqual(data, jsOldLink)) {
                index = idx;
                return;
            }
        });
        let updatedLinks = links.delete(index);
        let updatedLinkGroup = linkGroup.set('links', updatedLinks);
        let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
        this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
    },
    saveChanges() {
        let institutionId = this.state.institutionId;
        let linkGroupPath = getUrlPathPrefix() + "sys/preferences/institution/" + institutionId;
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            contentType: 'application/json',
            type: 'PUT',
            data: JSON.stringify(this.state.linkGroups),
            success: function(linkGroups) {
                let spanStyle = {
                    color: '#6DA487'
                };
                this.setState({
                    hasChanges: false,
                    saveButtonText: <span style={spanStyle}><span className="glyphicon glyphicon-ok"></span>SAVED</span>
                })
            }.bind(this),
            error: function(xhr, status, err) {
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
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span class="glyphicon glyphicon-cog"></span>Navigation Configuration</h1>
                </div>

                <div className="instconfig">
                    <LinkGroups linkGroups={this.state.linkGroups}
                                expandedLinkGroup={this.state.expandedLinkGroup}/>

                    <LinkGroupLinks linkGroups={this.state.linkGroups}
                                    expandedLinkGroup={this.state.expandedLinkGroup}
                                    topGroupSelected={this.state.topGroupSelected}/>

                </div>

                <div className="buttonbar">
                    <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                </div>
            </div>
        )
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('page-content')
);

export default InstitutionConfig;