var MyOrders = React.createClass({
    render() {
        return (
            <div className="kwidget">
                <h2>
                    <span className="glyphicon glyphicon-euro"></span>
                    <span>My Orders</span>
                </h2>
                <table>
                    <tbody>
                        <tr>
                            <th>Vendor</th>
                            <th>Description</th>
                            <th>Date Created</th>
                            <th>Status</th>
                            <th>More</th>
                        </tr>
                        <tr>
                            <td>Dell Computers</td>
                            <td>New computer for John Doe</td>
                            <td>05/16/2015</td>
                            <td className="status requested">Requested</td>
                            <td><span className="more glyphicon glyphicon-menu-down"></span></td>
                        </tr>
                        <tr>
                            <td>Dell Computers</td>
                            <td>New computer for John Doe</td>
                            <td>05/16/2015</td>
                            <td className="status canceled">Canceled</td>
                            <td><span className="more glyphicon glyphicon-menu-down"></span></td>
                        </tr>
                        <tr>
                            <td>Dell Computers</td>
                            <td>New computer for John Doe</td>
                            <td>05/16/2015</td>
                            <td className="status paid">Paid</td>
                            <td><span className="more glyphicon glyphicon-menu-down"></span></td>
                        </tr>
                        <tr>
                            <td>Dell Computers</td>
                            <td>New computer for John Doe</td>
                            <td>05/16/2015</td>
                            <td className="status open">Open</td>
                            <td><span className="more glyphicon glyphicon-menu-down"></span></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        )
    }
});

export default MyOrders;