import {buildSortableDropHandler} from './institutionconfigutils.js';
import {buildKeyFromLabel} from '../../sys/utils.js';
import Immutable from 'immutable';

let LinkGroupLinks = React.createClass({
    render() {
        let linkGroupLinkElements = this.props.linkGroups.map((linkGroup, index) => {
            let id = buildKeyFromLabel(linkGroup.get('label'))
            return <SubLinkGroup key={'subLinkGroup-' + id} id={id} links={linkGroup.get('links')} groupIndex = {index} groupLabel={linkGroup.get('label')} expandedLinkGroup={this.props.expandedLinkGroup} linkGroups={this.props.linkGroups}/>
        });
        let className = this.props.topGroupSelected ? 'top-selected' : '';
        return <div id="linkGroupLinksList" className={className}>{linkGroupLinkElements}</div>;
    }
});

let SubLinkGroup = React.createClass({
    contextTypes: {
        addNewCustomLink: React.PropTypes.func,
        updateExistingCustomLink: React.PropTypes.func
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
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom', type: 'activities'})
        };
    },
    openAddCustomLink() {
        this.setState({
            customLinkFormOpen: !this.state.customLinkFormOpen,
            errors: [],
            newLink: Immutable.Map({label: '', link: '', linkType: 'custom', type: 'activities'})
        });
    },
    addCustomLink() {
        if (this.state.newLink.get('label').trim() && this.state.newLink.get('link').trim()) {
            this.context.addNewCustomLink(this.props.groupIndex, this.state.newLink);
            this.setState({
                    customLinkFormOpen: false,
                    errors: [],
                    newLink: Immutable.Map({label: '', link: '', linkType: 'custom', type: 'activities'})
            });
        } else {
            let errors = [];
            if (!this.state.newLink.get('label').trim()) {
                errors.push('label');
            }
            if (!this.state.newLink.get('link').trim()) {
                errors.push('link');
            }
            this.setState({errors: errors});
        }
    },
    openUpdateCustomLink(link) {
        this.setState({
            customLinkFormOpen: true,
            errors: [],
            newLink: link,
            oldLink: link,
            update: true
        });
    },
    updateCustomLink() {
        if (this.state.newLink.get('label').trim() && this.state.newLink.get('link').trim()) {
            this.context.updateExistingCustomLink(this.props.groupIndex, this.state.oldLink, this.state.newLink);
            this.replaceState({
                customLinkFormOpen: false,
                errors: [],
                newLink: Immutable.Map({label: '', link: '', linkType: 'custom', type: 'activities'})
            });
        } else {
            let errors = [];
            if (!this.state.newLink.get('label').trim()) {
                errors.push('label');
            }
            if (!this.state.newLink.get('link').trim()) {
                errors.push('link');
            }
            this.setState({errors: errors});
        }
    },
    updateNewLinkValue(key, event) {
        let value = $(event.target).val();
        let updatedNewLink = this.state.newLink.set(key, value);
        this.setState({newLink: updatedNewLink});
    },
    render() {
        let divClassName = 'admin-sublinks';
        if (this.props.groupLabel !== this.props.expandedLinkGroup) {
            divClassName += ' collapse';
        }
        let formClass = this.state.customLinkFormOpen ? 'customLinkForm' : 'customLinkForm hidden';
        let labelClass = this.state.errors.indexOf('label') > -1 ? 'error' : '';
        let linkClass = this.state.errors.indexOf('link') > -1 ? 'error' : '';

        let formActionFunction = this.addCustomLink;
        let formActionText = 'Add';
        if (this.state.update) {
            formActionFunction = this.updateCustomLink;
            formActionText = 'Update';
        }
        return (
            <div id={this.props.id + "-menu"} className={divClassName}>
                <div className="addCustomLink">
                    <button className="btn btn-default" onClick={this.openAddCustomLink}><span className="glyphicon glyphicon-plus"></span>Add Custom Link</button>
                    <div className={formClass}>
                        <div><label>LINK NAME:</label></div>
                        <div><input className={labelClass} type="text" value={this.state.newLink.get('label')} onChange={this.updateNewLinkValue.bind(null, 'label')}/></div>
                        <div><label>URL:</label></div>
                        <div><input className={linkClass} type="text" value={this.state.newLink.get('link')} onChange={this.updateNewLinkValue.bind(null, 'link')}/></div>
                        <div>
                            <input checked={this.state.newLink.get('type') === 'activities'} type="radio" value="activities" id={this.props.id + "-activities-radio"} onChange={this.updateNewLinkValue.bind(null, 'type')}/>
                            <label htmlFor={this.props.id + "-activities-radio"}>Activities</label>
                            <input checked={this.state.newLink.get('type') === 'reference'} type="radio" value="reference" id={this.props.id + "-reference-radio"} onChange={this.updateNewLinkValue.bind(null, 'type')}/>
                            <label htmlFor={this.props.id + "-reference-radio"}>Reference</label>
                            <input checked={this.state.newLink.get('type') === 'administration'} type="radio" value="administration" id={this.props.id + "-administration-radio"} onChange={this.updateNewLinkValue.bind(null, 'type')}/>
                            <label htmlFor={this.props.id + "-administration-radio"}>Administration</label>
                        </div>
                        <div>
                            <button className="btn btn-green" onClick={formActionFunction}>{formActionText}</button>
                            <button className="btn btn-default" onClick={this.openAddCustomLink}>Cancel</button>
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
        let linksWithoutCurrentType = this.props.linkGroups.get(index).get('links').filter((link) => {
            return link.get('type') !== self.props.type;
        });
        let updatedLinks = links.concat(linksWithoutCurrentType);
        let updatedLinkGroups = this.props.linkGroups.set(index, this.props.linkGroups.get(index).set('links', updatedLinks));
        this.context.updateLinkGroups(updatedLinkGroups);
    },
    render() {
        let self = this;
        let linksForType = this.props.links.filter((link) => {
            return link.get('type') === self.props.type;
        });
        return (
            <div>
                <h4>{this.props.type}</h4>
                <SubLinkTypeLinks links={linksForType}
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
        if (this.props.links && this.props.links.count() > 0) {
            buildSortableDropHandler(id, self, 'links', 'updateSublinkTypeLinks');
        }
    },
    render() {
        let linkElements = []
        if (this.props.links && this.props.links.count() > 0) {
            linkElements = this.props.links.map((link, idx) => {
                let edit;
                if (link.get('linkType') === 'custom') {
                    edit = <span className="editLink" onClick={this.context.openUpdateCustomLink.bind(null, link)}>edit</span>;
                }
                return (
                <li key={idx}>
                        <span className="list-group-item">
                            <span className="move"></span>
                            {link.get('label')}
                            <div className="actions">{edit}</div>
                        </span>
                </li>);
            });
        } else {
            linkElements = <li><span className="list-group-item empty"></span></li>;
        }

        let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        let linkList = <ul id={id}>{linkElements}</ul>;

        return linkList
    }
});

export default LinkGroupLinks;