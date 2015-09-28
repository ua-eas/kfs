import KfsUtils from './utils.js';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroupModel: []};
    },
    componentWillMount() {
        let linkGroupPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroupModel: linkGroups});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    updateLinkGroupModel(currentLinkGroupModel) {
        this.setState({linkGroupModel: currentLinkGroupModel});
    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroupModel} updateLinkGroupModel={this.updateLinkGroupModel}/>
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
                    console.log("doing jQuery update");
                    var startIndex = ui.item.data("startindex");
                    var newIndex = ui.item.index();
                    let currentLinkGroupModel = self.props.linkGroups;
                    if (newIndex != startIndex) {
                        currentLinkGroupModel.splice(newIndex, 0, currentLinkGroupModel.splice(startIndex, 1)[0] );
                        self.props.updateLinkGroupModel(currentLinkGroupModel);
                    }
                }
            });
            ele.disableSelection();
        }
    },
    render() {
        console.log("doing react render - LinkGroups");
        let linkGroupElements = this.props.linkGroups.map((linkGroup) => {
            return <LinkGroup linkGroup={linkGroup} key={KfsUtils.buildKeyFromLabel(linkGroup.label)}/>
        });
        return <ul id="sortable">{linkGroupElements}</ul>;
    }
});

let LinkGroup = React.createClass({
    render() {
        return <li class="">{this.props.linkGroup.label}</li>
    }
});

React.render(
    <InstitutionConfig/>,
    document.getElementById('view_div')
);

export default InstitutionConfig;