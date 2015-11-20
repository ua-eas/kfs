import React from 'react/addons';
import KfsUtils from '../../sys/utils.js';

var InstitutionConfigSidebar = React.createClass({
    componentDidMount() {
        if (window.location.hash === '#/' || window.location.hash === '') {
            $('#sidebar a[href="#/navigation"]').siblings(' div.indicator').addClass('active');
        }
    },
    updateIndicator(event) {
        $('#sidebar div.active').removeClass('active');
        $(event.target).siblings('div.indicator').addClass('active');
    },
    render() {
        let home = KfsUtils.getUrlPathPrefix();
        let hash = window.location.hash;
        let linkObjs = [
            {'label': 'Dashboard', 'url': home},
            {'label': 'Logo Upload', 'url': '#/logo'},
            {'label': 'Navigation Configuration', 'url': '#/navigation'},
            {'label': 'Menu Configuration', 'url': '#/menu'}
        ];
        let links = linkObjs.map((link, index) => {
            let indicator = hash === link.url ? <div className="active indicator"></div> : <div className="indicator"></div>;
            return <li key={'nav-'+index} className="panel list-item"><a href={link.url} onClick={this.updateIndicator}>{link.label}</a>{indicator}</li>;
        });
        return (
            <div className="inst-config-sidebar">
                <ul id="link-groups" className="nav list-group">
                    <li className="list-item"></li>
                    {links}
                </ul>
            </div>
        )
    }
});

React.render(
    <InstitutionConfigSidebar/>,
    document.getElementById('sidebar')
);

export default InstitutionConfigSidebar;