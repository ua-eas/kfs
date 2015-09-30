import LinkGroups from './linkgroups.jsx';
import LinkGroupLinks from './linkgrouplinks.jsx';
import {getUrlPathPrefix} from '../../sys/utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroups: new Immutable.List(), expandedLinkGroup: ""};
    },
    componentWillMount() {
        let linkGroupPath = getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroups: Immutable.fromJS(linkGroups)});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    toggleLinkGroup(label) {
        if (this.state.expandedLinkGroup === label) {
            this.setState({expandedLinkGroup: ""});
        } else {
            this.setState({expandedLinkGroup: label});
        }
    },
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: linkGroups});
    },
    updateLinkGroupName(linkGroupIndex, newName) {
        let linkGroup = this.state.linkGroups.get(linkGroupIndex);
        let updatedLinkGroup = linkGroup.set('label', newName);
        let updatedLinkGroups = this.state.linkGroups.set(linkGroupIndex, updatedLinkGroup);
        this.setState({linkGroups: updatedLinkGroups});
    },
    childContextTypes: {
        toggleLinkGroup: React.PropTypes.func,
        updateLinkGroups: React.PropTypes.func,
        updateLinkGroupName: React.PropTypes.func
    },
    getChildContext() {
        return {
            toggleLinkGroup: this.toggleLinkGroup,
            updateLinkGroups: this.updateLinkGroups,
            updateLinkGroupName: this.updateLinkGroupName
        }
    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroups}
                            expandedLinkGroup={this.state.expandedLinkGroup}/>

                <LinkGroupLinks linkGroups={this.state.linkGroups}
                                expandedLinkGroup={this.state.expandedLinkGroup}/>
            </div>
        )
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('institutionconfig')
);

export default InstitutionConfig;