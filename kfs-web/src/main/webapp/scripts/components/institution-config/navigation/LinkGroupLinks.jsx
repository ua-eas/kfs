/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import React, {Component} from 'react';
import {buildLinkSortableDropHandler, isScrolledIntoView, validateForm} from '../institutionConfigUtils.js';
import {buildKeyFromLabel} from '../../../sys/utils.js';
import Immutable from 'immutable';


export default class LinkGroupLinks extends Component {
    constructor(props) {
        super(props);
    }

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
                                 linkGroupLabels={linkGroupLabels}
                                 stateMaintenance={this.props.stateMaintenance}/>
        });
        let className = this.props.topGroupSelected ? 'top-selected' : '';
        return <div id="sub-item-list" className={className}>{linkGroupLinkElements}</div>;
    }
}

class SubLinkGroup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            customLinkFormOpen: false,
            errors: [],
            errorMessages: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
            newLinkType: 'activities'
        };

        this.openAddCustomLink = this.openAddCustomLink.bind(this);
        this.addCustomLink = this.addCustomLink.bind(this);
        this.stateOpenUpdateCustomLink = this.stateOpenUpdateCustomLink.bind(this);
        this.stateUpdateCustomLink = this.stateUpdateCustomLink.bind(this);
        this.stateDeleteCustomLink = this.stateDeleteCustomLink.bind(this);
        this.updateNewLinkValue = this.updateNewLinkValue.bind(this);
        this.updateNewLinkType = this.updateNewLinkType.bind(this);
        this.updateNewLinkNewTarget = this.updateNewLinkNewTarget.bind(this);
        this.updateLinkGroup = this.updateLinkGroup.bind(this);
    }

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
    }

    addCustomLink() {
        let errorObj = validateForm(this.state.newLink.get('label'), this.state.newLink.get('link'));

        if (errorObj.errors.length < 1) {
            this.props.stateMaintenance.stateAddNewCustomLink(this.props.groupIndex, this.state.newLink, this.state.newLinkType);
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
    }

    stateOpenUpdateCustomLink(link, type) {
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
    }

    stateUpdateCustomLink() {
        let errorObj;
        if (this.state.newLink.get('linkType') === 'custom') {
            errorObj = validateForm(this.state.newLink.get('label'), this.state.newLink.get('link'));
        }

        if (!errorObj || errorObj.errors.length < 1) {
            this.props.stateMaintenance.stateUpdateExistingCustomLink(this.props.groupIndex, this.state.oldLink, this.state.oldLinkType,
                this.state.moveToGroupIndex, this.state.newLink, this.state.newLinkType);
            this.state = {
                customLinkFormOpen: false,
                errors: [],
                errorMessages: [],
                newLink: Immutable.Map({label: '', link: '', linkType: 'custom'}),
                newLinkType: 'activities'
            };
        } else {
            this.setState(errorObj);
        }
    }

    stateDeleteCustomLink(groupIndex, link, type) {
        this.props.stateMaintenance.stateDeleteExistingCustomLink(groupIndex, type, link);
        this.state = {
            customLinkFormOpen: false,
            errors: [],
            errorMessages: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom', newTarget: false}),
            newLinkType: 'activities'
        };
    }

    updateNewLinkValue(key, event) {
        let value = $(event.target).val();
        let updatedNewLink = this.state.newLink.set(key, value);
        this.setState({newLink: updatedNewLink});
    }

    updateNewLinkType(event) {
        let type = $(event.target).val();
        this.setState({'newLinkType': type});
    }

    updateNewLinkNewTarget(event) {
        let newTarget = event.target.checked;
        let updatedNewLink = this.state.newLink.set('newTarget', newTarget);
        this.setState({newLink: updatedNewLink});
    }

    updateLinkGroup(event) {
        let group = $(event.target).val();
        this.setState({'moveToGroupIndex': group});
    }

    render() {
        let stateMaintenance = this.props.stateMaintenance;
        stateMaintenance['stateOpenUpdateCustomLink'] = this.stateOpenUpdateCustomLink;
        stateMaintenance['stateUpdateCustomLink'] = this.stateUpdateCustomLink;
        stateMaintenance['stateDeleteCustomLink'] = this.stateDeleteCustomLink;

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
            return <option key={index} value={index}>{linkGroupLabel.toUpperCase()}</option>;
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
        let newTargetToggle;
        let formActionFunction = this.stateUpdateCustomLink;
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
                        </div>
                    </div>
                </div>
                <SubLinkType
                    groupLabel={this.props.groupLabel}
                    links={this.props.links}
                    type='activities'
                    linkGroups={this.props.linkGroups}
                    stateMaintenance={stateMaintenance}
                />
                <SubLinkType
                    groupLabel={this.props.groupLabel}
                    links={this.props.links}
                    type='reference'
                    linkGroups={this.props.linkGroups}
                    stateMaintenance={stateMaintenance}
                />
                <SubLinkType
                    groupLabel={this.props.groupLabel}
                    links={this.props.links}
                    type='administration'
                    linkGroups={this.props.linkGroups}
                    stateMaintenance={stateMaintenance}
                />
            </div>
        )
    }
}

class SubLinkType extends Component {
    constructor(props) {
        super(props);
        this.stateUpdateSublinkTypeLinks = this.stateUpdateSublinkTypeLinks.bind(this);
    }

    stateUpdateSublinkTypeLinks(links, groupLabel) {
        let index = this.props.linkGroups.findIndex((linkGroup) => linkGroup.get('label') === groupLabel);
        let updatedLinks = this.props.linkGroups.get(index).set('links', links);
        let updatedLinkGroups = this.props.linkGroups.set(index, updatedLinks);
        this.props.stateMaintenance.stateUpdateLinkGroups(updatedLinkGroups);
    }

    render() {
        let stateMaintenance = this.props.stateMaintenance;
        stateMaintenance['stateUpdateSublinkTypeLinks'] = this.stateUpdateSublinkTypeLinks;
        const linksForType = this.props.links.get(this.props.type);
        const groupIndex = this.props.linkGroups.findIndex((linkGroup) => linkGroup.get('label') === this.props.groupLabel);
        return (
            <div>
                <h4>{this.props.type}</h4>
                <SubLinkTypeLinks
                    allLinks={this.props.links}
                    links={linksForType}
                    type={this.props.type}
                    groupLabel={this.props.groupLabel}
                    groupIndex={groupIndex}
                    stateMaintenance={stateMaintenance}
                />
            </div>
        )
    }
}

class SubLinkTypeLink extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const deleteLink = (this.props.link.get("linkType") === "custom")
            ? <span className="deleteLink" onClick={this.props.stateMaintenance.stateDeleteCustomLink.bind(null, this.props.groupIndex, this.props.link, this.props.type)}></span>
            : null;
        const key = `subLinkTypeLink-${this.props.groupIndex}-${this.props.type}-${this.props.idx}`;
        return (
            <li key={key}>
            <span className="list-group-item">
                <span className="move"></span>
                {this.props.link.get('label')}
                <div className="actions">
                    <span className="editLink"
                          onClick={this.props.stateMaintenance.stateOpenUpdateCustomLink.bind(null, this.props.link, this.props.type)}></span>
                    {deleteLink}
                </div>
            </span>
            </li>
        );
    }
}

class SubLinkTypeLinks extends Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        const id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        const connectWithClass = ".sortable-" + buildKeyFromLabel(this.props.groupLabel);
        buildLinkSortableDropHandler(id, connectWithClass, this, 'allLinks', 'stateUpdateSublinkTypeLinks', this.props.groupLabel);
    }

    render() {
        const linkElements =  (this.props.links && this.props.links.count() > 0)
            ? this.props.links.map((link, idx) => <SubLinkTypeLink key={idx} idx={idx} link={link} groupIndex={this.props.groupIndex} type={this.props.type} stateMaintenance={this.props.stateMaintenance}/>)
            : (<li key="subLinkTypeLink-undefined"><span className="list-group-item empty"></span></li>);

        const id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        const className = "sortable-" + buildKeyFromLabel(this.props.groupLabel);
        const linkList = <ul id={id} className={className} data-type={this.props.type}>{linkElements}</ul>;

        return linkList;
    }
}
