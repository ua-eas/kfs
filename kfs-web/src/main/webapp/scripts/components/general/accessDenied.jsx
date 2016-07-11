import React from 'react';
import { render } from 'react-dom';

let AccessDenied = React.createClass({
    render() {
        return <div>Sorry. Access Denied</div>
    }
});

render(
    <AccessDenied/>,
    document.getElementById('page-content')
);

export default AccessDenied;