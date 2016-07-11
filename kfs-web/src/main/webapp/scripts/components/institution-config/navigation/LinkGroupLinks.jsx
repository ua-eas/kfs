import React from 'react';
import {buildLinkSortableDropHandler, isScrolledIntoView, validateForm} from '../institutionConfigUtils.js';
import {buildKeyFromLabel} from '../../../sys/utils.js';
import Immutable from 'immutable';


let LinkGroupLinks = React.createClass({
    render() {
        let linkGroupLabels = this.props.linkGroups.map((linkGroup) => {
            return linkGroup.get('label');
        });
        let linkGroupLinkElements = this.props.linkGroups.map((linkGroup, index) => {
            let id = buildKeyFromLabel(linkGroup.get('label'));
            return <SubLinkGroup key={'subLinkGroup-' + id}
                                 id={id} links={linkGroup.get('links')}
                                 groupIndex = {index}
                                 groupLabel={linkGroup.get('label')}
                                 expandedLinkGroup={this.props.expandedLinkGroup}
                                 linkGroups={this.props.linkGroups}
                                 linkGroupLabels={linkGroupLabels}/>
        });
        let className = this.props.topGroupSelected ? 'top-selected' : '';
        return <div id="sub-item-list" className={className}>{linkGroupLinkElements}</div>;
    }
});

let SubLinkGroup = React.createClass({
    contextTypes: {
        addNewCustomLink: React.PropTypes.func,
        updateExistingCustomLink: React.PropTypes.func,
        deleteExistingCustomLink: React.PropTypes.func
    },
    childContextTypes: {
        openUpdateCustomLink: React.PropTypes.func,
        updateCustomLink: React.PropTypes.func
    },
    getChildContext() {
        return {
            openUpdateCustomLink: this.openUpdateCustomLink,
            updateCustomLink: this.updateCustomLink
        }
    },
    getInitialState() {
        return {
            customLinkFormOpen: false,
            errors: [],
            errorMessages: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
            newLinkType: 'activities'
        };
    },
    openAddCustomLink() {
        this.setState({
            customLinkFormOpen: !this.state.customLinkFormOpen,
            errors: [],
            errorMessages: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
            newLinkType: 'activities',
            moveToGroupIndex: this.props.groupIndex,
            update: false
        });
    },
    addCustomLink() {
        let errorObj = validateForm(this.state.newLink.get('label'), this.state.newLink.get('link'));

        if (errorObj.errors.length < 1) {
            this.context.addNewCustomLink(this.props.groupIndex, this.state.newLink, this.state.newLinkType);
            this.setState({
                customLinkFormOpen: false,
                errors: [],
                errorMessages: [],
                newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
                newLinkType: 'activities'
            });
        } else {
            this.setState(errorObj);
        }
    },
    openUpdateCustomLink(link, type) {
        this.setState({
            customLinkFormOpen: true,
            errors: [],
            errorMessages: [],
            newLink: link,
            newLinkType: type,
            oldLink: link,
            oldLinkType: type,
            moveToGroupIndex: this.props.groupIndex,
            update: true
        });
        if (!isScrolledIntoView($('.active .add-custom-link'))) {
            $('html,body').animate({scrollTop: 100}, 1000);
        }
    },
    updateCustomLink() {
        let errorObj;
        if (this.state.newLink.get('linkType') === 'custom') {
            errorObj = validateForm(this.state.newLink.get('label'), this.state.newLink.get('link'));
        }

        if (!errorObj || errorObj.errors.length < 1) {
            this.context.updateExistingCustomLink(this.props.groupIndex, this.state.oldLink, this.state.oldLinkType,
                this.state.moveToGroupIndex, this.state.newLink, this.state.newLinkType);
            this.replaceState({
                customLinkFormOpen: false,
                errors: [],
                errorMessages: [],
                newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
                newLinkType: 'activities'
            });
        } else {
            this.setState(errorObj);
        }
    },
    deleteCustomLink() {
        this.context.deleteExistingCustomLink(this.props.groupIndex, this.state.oldLinkType, this.state.oldLink);
        this.replaceState({
            customLinkFormOpen: false,
            errors: [],
            errorMessages: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom', newTarget: false}),
            newLinkType: 'activities'
        });
    },
    updateNewLinkValue(key, event) {
        let value = $(event.target).val();
        let updatedNewLink = this.state.newLink.set(key, value);
        this.setState({newLink: updatedNewLink});
    },
    updateNewLinkType(event) {
        let type = $(event.target).val();
        this.setState({'newLinkType': type});
    },
    updateNewLinkNewTarget(event) {
        let newTarget = event.target.checked;
        let updatedNewLink = this.state.newLink.set('newTarget', newTarget);
        this.setState({newLink: updatedNewLink});
    },
    updateLinkGroup(event) {
        let group = $(event.target).val();
        this.setState({'moveToGroupIndex': group});
    },
    render() {
        let divClassName = 'admin-sublinks';
        if (this.props.groupLabel !== this.props.expandedLinkGroup) {
            divClassName += ' collapse';
        } else {
            divClassName += ' active';
        }
        let formClass = this.state.customLinkFormOpen ? 'customLinkForm form' : 'customLinkForm form hidden';
        let labelClass = this.state.errors.indexOf('label') > -1 ? 'error' : '';
        let linkClass = this.state.errors.indexOf('link') > -1 ? 'error' : '';

        let groupSelectItems = this.props.linkGroupLabels.map((linkGroupLabel, index) => {
            return <option key={index} value={index}>{linkGroupLabel}</option>;
        });

        let errorMessage;
        if (this.state.errorMessages && this.state.errorMessages.length > 0) {
            let messages = this.state.errorMessages.map(function(message, index) {
                return <li key={index}>{message}</li>
            });
            errorMessage = <ul className="errorMessages">{messages}</ul>;
        }

        let editLinkName;
        let editLinkURL;
        let deleteButton;
        let newTargetToggle;
        let formActionFunction = this.updateCustomLink;
        let formActionText = 'Update';
        if (this.state.newLink.get('linkType') === 'custom') {
            editLinkName = (
                <div>
                    <div><label>LINK NAME:</label></div>
                    <div><input className={labelClass} type="text" value={this.state.newLink.get('label')} onChange={this.updateNewLinkValue.bind(null, 'label')}/></div>
                </div>
            );
            editLinkURL = (
                <div>
                    <div><label>URL:</label></div>
                    <div><input className={linkClass} type="text" value={this.state.newLink.get('link')} onChange={this.updateNewLinkValue.bind(null, 'link')}/></div>
                </div>
            );

            if (!this.state.update) {
                formActionFunction = this.addCustomLink;
                formActionText = 'Add';
            } else {
                deleteButton = <button className="btn btn-default" onClick={this.deleteCustomLink}>Delete</button>
            }
        } else if (this.state.newLink.get('linkType') === 'kfs') {

            newTargetToggle = (
                <div>
                    <div>
                        <input checked={this.state.newLink.get('newTarget')} type="checkbox" value="newTarget" onChange={this.updateNewLinkNewTarget}/>
                        <label>Open In New Window</label>
                    </div>
                </div>
            );
        }

        return (
            <div id={this.props.id + "-menu"} className={divClassName}>
                <div className="add-custom-link">
                    <button className="btn btn-default" onClick={this.openAddCustomLink}><span className="glyphicon glyphicon-plus"></span>Add Custom Link</button>
                    <div className={formClass}>
                        {errorMessage}
                        {editLinkName}
                        {editLinkURL}
                        <div>
                            <input checked={this.state.newLinkType === 'activities'} type="radio" value="activities" id={this.props.id + "-activities-radio"} onChange={this.updateNewLinkType}/>
                            <label htmlFor={this.props.id + "-activities-radio"}>Activities</label>
                            <input checked={this.state.newLinkType === 'reference'} type="radio" value="reference" id={this.props.id + "-reference-radio"} onChange={this.updateNewLinkType}/>
                            <label htmlFor={this.props.id + "-reference-radio"}>Reference</label>
                            <input checked={this.state.newLinkType === 'administration'} type="radio" value="administration" id={this.props.id + "-administration-radio"} onChange={this.updateNewLinkType}/>
                            <label htmlFor={this.props.id + "-administration-radio"}>Administration</label>
                        </div>
                        {newTargetToggle}
                        <div><label>GROUP:</label></div>
                        <div>
                            <select value={this.state.moveToGroupIndex} onChange={this.updateLinkGroup}>{groupSelectItems}</select>
                        </div>
                        <div>
                            <button className="btn btn-green" onClick={formActionFunction}>{formActionText}</button>
                            <button className="btn btn-default" onClick={this.openAddCustomLink}>Cancel</button>
                            {deleteButton}
                        </div>
                    </div>
                </div>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='activities' linkGroups={this.props.linkGroups}/>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='reference' linkGroups={this.props.linkGroups}/>
                <SubLinkType groupLabel={this.props.groupLabel} links={this.props.links} type='administration' linkGroups={this.props.linkGroups}/>
            </div>
        )
    }
});

let SubLinkType = React.createClass({
    contextTypes: {
        updateLinkGroups: React.PropTypes.func
    },
    childContextTypes: {
        updateSublinkTypeLinks: React.PropTypes.func
    },
    getChildContext() {
        return {
            updateSublinkTypeLinks: this.updateSublinkTypeLinks
        }
    },
    updateSublinkTypeLinks(links) {
        let self = this;
        let index = this.props.linkGroups.findIndex(function(linkGroup) {
            return linkGroup.get('label') === self.props.groupLabel;
        });
        let updatedLinks = this.props.linkGroups.get(index).set('links', links);
        let updatedLinkGroups = this.props.linkGroups.set(index, updatedLinks);
        this.context.updateLinkGroups(updatedLinkGroups);
    },
    render() {
        let self = this;
        let linksForType = this.props.links.get(self.props.type);
        return (
            <div>
                <h4>{this.props.type}</h4>
                <SubLinkTypeLinks allLinks={this.props.links}
                                  links={linksForType}
                                  type={this.props.type}
                                  groupLabel={this.props.groupLabel}/>
            </div>
        )
    }
});

let SubLinkTypeLinks = React.createClass({
    contextTypes: {
        updateSublinkTypeLinks: React.PropTypes.func,
        openUpdateCustomLink: React.PropTypes.func,
        updateCustomLink: React.PropTypes.func
    },
    componentDidMount() {
        let self = this;
        let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        let connectWithClass = ".sortable-" + buildKeyFromLabel(this.props.groupLabel);
        buildLinkSortableDropHandler(id, connectWithClass, self, 'allLinks', 'updateSublinkTypeLinks');
    },
    render() {
        let linkElements = []
        if (this.props.links && this.props.links.count() > 0) {
            linkElements = this.props.links.map((link, idx) => {
                return (
                    <li key={idx}>
                            <span className="list-group-item">
                                <span className="move"></span>
                                {link.get('label')}
                                <div className="actions">
                                    <span className="editLink" onClick={this.context.openUpdateCustomLink.bind(null, link, this.props.type)}>edit</span>
                                </div>
                            </span>
                    </li>
                );
            });
        } else {
            linkElements = <li><span className="list-group-item empty"></span></li>;
        }

        let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        let className = "sortable-" + buildKeyFromLabel(this.props.groupLabel);
        let linkList = <ul id={id} className={className} data-type={this.props.type}>{linkElements}</ul>;

        return linkList
    }
});

export default LinkGroupLinks;