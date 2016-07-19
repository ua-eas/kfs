import React from 'react';
import LinkGroups from './LinkGroups.jsx';
import LinkGroupLinks from './LinkGroupLinks.jsx';
import {getUrlPathPrefix} from '../../../sys/utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let NavigationConfig = React.createClass({
    childContextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroups: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func,
        addNewLinkGroup: React.PropTypes.func,
        cancelAddNewLinkGroup: React.PropTypes.func,
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
            cancelAddNewLinkGroup: this.cancelAddNewLinkGroup,
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
        let linkGroupPath = getUrlPathPrefix() + "api/v1/sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            cache: false,
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
    componentDidUpdate() {
        if (this.state.expandedLinkGroup) {
            let item = $('#item-list .item.active');

            // Figure out how far down the bottom of the group is
            let itemLocation = item.offset().top;
            let itemHeight = item.height();
            let itemBottom = itemLocation + itemHeight;

            let subListItem = $('#sub-item-list .admin-sublinks.active');

            // Reset margin so that calculations are accurate (in case it has already been offset)
            subListItem.css('margin-top', '0px');

            // Figure out how far down the bottom of the links list is
            let subListItemLocation =  subListItem.offset().top;
            let subListItemHeight = subListItem.height();
            let subListItemBottom = subListItemLocation + subListItemHeight;

            // If the links list is not as far down as the group then move it down so it lines up
            if (subListItemBottom < itemBottom) {
                subListItem.css('margin-top', (itemBottom - subListItemBottom) + 'px');
            }
        }
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
        let newLinkGroup = Immutable.fromJS({label: '', links: {}});
        let updatedLinkGroups = linkGroups.push(newLinkGroup);
        this.setState({linkGroups: updatedLinkGroups})
    },
    cancelAddNewLinkGroup() {
        let linkGroups = this.state.linkGroups;
        let updatedLinkGroups = linkGroups.pop();
        this.setState({linkGroups: updatedLinkGroups})
    },
    deleteLinkGroup(index) {
        let linkGroups = this.state.linkGroups;
        let updatedLinkGroups = linkGroups.splice(index, 1);
        this.setState({linkGroups: updatedLinkGroups, hasChanges: true});
    },
    addNewCustomLink(groupIndex, newLink, newLinkType) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let linksTypes = linkGroup.get('links');
        let links = linksTypes.get(newLinkType);
        if (!links) {
            links = Immutable.fromJS([]);
        }
        let updatedLinks = links.push(newLink);
        let updatedLinkType = linksTypes.set(newLinkType, updatedLinks);
        let updatedLinkGroup = linkGroup.set('links', updatedLinkType);
        let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
        this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
    },
    updateExistingCustomLink(groupIndex, oldLink, oldLinkType, updatedGroupIndex, updatedLink, updatedLinkType) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let linkTypes = linkGroup.get('links');
        let linksOldType = linkTypes.get(oldLinkType);
        let linkIndex = -1;

        let jsLinks = linksOldType.toJS();
        let jsOldLink = oldLink.toJS();
        _.each(jsLinks, function(data, idx) {
            if (_.isEqual(data, jsOldLink)) {
                linkIndex = idx;
                return;
            }
        });

        if (groupIndex !== updatedGroupIndex) {
            let moveToLinkGroup = linkGroups.get(updatedGroupIndex);

            let updatedLinks = linksOldType.delete(linkIndex);
            let updatedLinkTypes = linkTypes.set(oldLinkType, updatedLinks);

            let moveToLinkTypes = moveToLinkGroup.get('links');
            let moveToLinksType = moveToLinkTypes.get(updatedLinkType);
            if (!moveToLinksType) {
                moveToLinksType = Immutable.fromJS([]);
            }
            let updatedMoveToLinkType = moveToLinksType.push(updatedLink);
            let updatedMoveToLinkTypes = moveToLinkTypes.set(updatedLinkType, updatedMoveToLinkType);

            let updatedLinkGroup = linkGroup.set('links', updatedLinkTypes);
            let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);

            let updatedMoveToLinkGroup = moveToLinkGroup.set('links', updatedMoveToLinkTypes);
            let finalLinkGroups = updatedLinkGroups.set(updatedGroupIndex, updatedMoveToLinkGroup);

            this.setState({'linkGroups': finalLinkGroups, hasChanges: true});
        } else {
            let updatedLinkTypes;
            if (oldLinkType === updatedLinkType) {
                let updatedLinks = linksOldType.set(linkIndex, updatedLink);
                updatedLinkTypes = linkTypes.set(oldLinkType, updatedLinks);
            } else {
                // delete link from old type links
                let updatedLinksOldType = linksOldType.delete(linkIndex);

                // add link to new type links
                let linksUpdatedType = linkTypes.get(updatedLinkType);
                if (!linksUpdatedType) {
                    linksUpdatedType = Immutable.fromJS([]);
                }
                let updatedLinksUpdatedType = linksUpdatedType.push(updatedLink);

                // update links for both updated and old types
                let partialUpdatedLinkTypes = linkTypes.set(oldLinkType, updatedLinksOldType);
                updatedLinkTypes = partialUpdatedLinkTypes.set(updatedLinkType, updatedLinksUpdatedType);
            }

            let updatedLinkGroup = linkGroup.set('links', updatedLinkTypes);
            let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
            this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
        }
    },
    deleteExistingCustomLink(groupIndex, type, oldLink) {
        let linkGroups = this.state.linkGroups;
        let linkGroup = linkGroups.get(groupIndex);
        let linkTypes = linkGroup.get('links');
        let links = linkTypes.get(type);
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
        let updatedLinksType;
        if (updatedLinks.size < 1) {
            updatedLinksType = linkTypes.delete(type);
        } else {
            updatedLinksType = linkTypes.set(type, updatedLinks);
        }
        let updatedLinkGroup = linkGroup.set('links', updatedLinksType);
        let updatedLinkGroups = linkGroups.set(groupIndex, updatedLinkGroup);
        this.setState({'linkGroups': updatedLinkGroups, hasChanges: true});
    },
    saveChanges() {
        let institutionId = this.state.institutionId;
        let linkGroupPath = getUrlPathPrefix() + "api/v1/sys/preferences/institution/" + institutionId;
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            contentType: 'application/json',
            cache: false,
            type: 'PUT',
            data: JSON.stringify(this.state.linkGroups),
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
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span className="glyphicon glyphicon-cog"></span>Navigation Configuration</h1>
                </div>

                <div className="nav-config main">
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

export default NavigationConfig;