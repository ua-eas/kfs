import MyOrders from './myorders.jsx';

var Dashboard = React.createClass({
    render() {
        return (
            <div>
                <MyOrders/>
            </div>
        )
    }
});

if (document.getElementById('dashboard')) {
    React.render(
        <Dashboard/>,
        document.getElementById('dashboard')
    );
}

export default Dashboard;