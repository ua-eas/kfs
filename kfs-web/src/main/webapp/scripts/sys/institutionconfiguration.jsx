import KfsUtils from './utils.js';
import _ from 'lodash';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroups: []};
    },
    componentWillMount() {
        let linkGroupPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroups: linkGroups});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: linkGroups});

    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroups} updateLinkGroups={this.updateLinkGroups}/>
            </div>
        )
    }
});

let LinkGroups = React.createClass({
    componentDidMount() {
        let self = this;

        let ele = $("#sortable");
        if (ele) {
            ele.sortable({
                start: function (event, ui) {
                    $(ui.item).data("startindex", ui.item.index());
                },
                update: function (event, ui) {
                    event.stopPropagation();
                    var startIndex = ui.item.data("startindex");
                    var newIndex = ui.item.index();
                    let linkGroups = _.cloneDeep(self.props.linkGroups);
                    if (newIndex != startIndex) {
                        linkGroups.splice(newIndex, 0, linkGroups.splice(startIndex, 1)[0] );
                        $("#sortable").sortable('cancel');
                        self.props.updateLinkGroups(linkGroups);
                    }
                }
            });
            ele.disableSelection();
        }
    },
    render() {
        let linkGroupElements = this.props.linkGroups.map((linkGroup) => {
            return <LinkGroup linkGroup={linkGroup} key={KfsUtils.buildKeyFromLabel(linkGroup.label)}/>
        });
        return <ul id="sortable">{linkGroupElements}</ul>;
    }
});

let LinkGroup = React.createClass({
    render() {
        return <li className="linkgroup"><span className="move"></span>{this.props.linkGroup.label}</li>
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('institutionconfig')
);

export default InstitutionConfig;