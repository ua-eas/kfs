import React from 'react/addons';

var InstConfigSidebar = React.createClass({
    render() {
        return (
            <div className="inst-config-sidebar">
                <ul id="linkgroups" className="nav list-group">
                    <li className="list-item"></li>
                    <li className="panel list-item"><a href="#/navigationconfig">Navigation Configuration</a></li>
                    <li className="panel list-item"><a href="#/menuconfig">Menu Configuration</a></li>
                </ul>
            </div>
        )
    }
});

React.render(
    <InstConfigSidebar/>,
    document.getElementById('sidebar')
);

export default InstConfigSidebar;