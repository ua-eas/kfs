import KfsUtils from './utils.js';
import jqueryui from '../jquery-ui.min.js';

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
                console.table(linkGroups)
                this.setState({linkGroupModel: linkGroups});

            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    componentDidUpdate(prevProps, prevState) {
        let ele = $("#sortable");
        if (ele) {
            ele.sortable();
            ele.disableSelection();
        }
    },
    render() {
        return (
            <div className="instconfig">
                <LinkGroups linkGroups={this.state.linkGroupModel}/>
            </div>
        )
    }
});

let LinkGroups = React.createClass({
   render() {
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