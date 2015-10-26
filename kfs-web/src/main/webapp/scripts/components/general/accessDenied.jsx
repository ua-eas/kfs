let AccessDenied = React.createClass({
    render() {
        return <div>Sorry. Access Denied</div>
    }
});

React.render(
    <AccessDenied/>,
    document.getElementById('page-content')
);

export default AccessDenied;