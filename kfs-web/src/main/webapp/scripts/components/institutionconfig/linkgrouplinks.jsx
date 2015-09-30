import {buildSortableDropHandler} from './institutionconfigutils.js';
import {buildKeyFromLabel} from '../../sys/utils.js';

let LinkGroupLinks = React.createClass({
    render() {
        let linkGroupLinkElements = this.props.linkGroups.map((linkGroup) => {
            let id = buildKeyFromLabel(linkGroup.get('label'))
            return <SubLinkGroup key={'subLinkGroup-' + id} id={id} links={linkGroup.get('links')} groupLabel={linkGroup.get('label')} expandedLinkGroup={this.props.expandedLinkGroup} linkGroups={this.props.linkGroups}/>
        });
        return <div id="linkGroupLinksList">{linkGroupLinkElements}</div>;
    }
});

let SubLinkGroup = React.createClass({
    render() {
        let divClassName = 'admin-sublinks';
        if (this.props.groupLabel !== this.props.expandedLinkGroup) {
            divClassName += ' collapse';
        }
        return (
            <div id={this.props.id + "-menu"} className={divClassName}>
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
        updateSublinkTypeLinks: React.PropTypes.func
    },
    componentDidMount() {
        let self = this;
        let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
        buildSortableDropHandler(id, self, 'links', 'updateSublinkTypeLinks');
    },
    render() {
        let linkList = "";
        if (this.props.links) {
            let linkElements = this.props.links.map((link, idx) => {
                return <li key={idx}><span className="list-group-item"><span className="move"></span>{link.get('label')}</span></li>;
            });
            let id = "sortable-" + buildKeyFromLabel(this.props.groupLabel) + "-" + this.props.type;
            linkList = <ul id={id}>{linkElements}</ul>;
        }
        return linkList
    }
});

export default LinkGroupLinks;