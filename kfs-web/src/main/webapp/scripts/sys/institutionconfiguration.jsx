import KfsUtils from './utils.js';
import _ from 'lodash';
import Immutable from 'immutable';

let InstitutionConfig = React.createClass({
    getInitialState() {
        return {linkGroups: new Immutable.List()};
    },
    componentWillMount() {
        let linkGroupPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/groups";
        $.ajax({
            url: linkGroupPath,
            dataType: 'json',
            type: 'GET',
            success: function(linkGroups) {
                this.setState({linkGroups: new Immutable.List(linkGroups)});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    updateLinkGroups(linkGroups) {
        this.setState({linkGroups: new Immutable.List(linkGroups)});

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
    removeElementAt(list, removeIndex) {
        if (removeIndex === 0) {
            return list.slice(1);
        } else if (list.size === (removeIndex + 1)) {
            return list.slice(0, -1);
        } else {
            return list.slice(0, removeIndex - 1).concat(list.slice(removeIndex+1));
        }
    },
    insertElementAt(list, ele, addIndex) {
        let eleList = Immutable.List.of(ele);
        if (addIndex === 0) {
            return eleList.concat(list);
        } else if (list.size === (addIndex + 1)) {
            return list.concat(eleList);
        } else {
            return list.slice(0, addIndex).concat(eleList).concat(list.slice(addIndex));
        }
    },
    moveElement(list, fromIndex, toIndex) {
        let movingElement = list.get(fromIndex)
        let updatedList = this.insertElementAt(this.removeElementAt(list, fromIndex), movingElement, toIndex);
        return updatedList;
    },
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
                    let startIndex = ui.item.data("startindex");
                    let newIndex = ui.item.index();
                    if (newIndex != startIndex) {
                        let updatedLinkGroups = self.moveElement(self.props.linkGroups, startIndex, newIndex);
                        $("#sortable").sortable('cancel');
                        self.props.updateLinkGroups(updatedLinkGroups);
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