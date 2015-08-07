import KfsUtils from './sys/utils.js'
import moment from 'moment'

var MyOrders = React.createClass({
    getInitialState() {
        return {reqs: []}
    },
    componentWillMount() {
        let path = KfsUtils.getUrlPathPrefix() + "purap/myorders"

        $.ajax({
            url: path,
            dataType: 'json',
            type: 'GET',
            success: function(reqs) {
                this.setState({reqs: reqs});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    render() {
        let rows = [];
        this.state.reqs.map(function(req, i) {
            let statusClass = "status " + req.workflowDocumentStatusCode.toLowerCase()
            rows.push(
                <tr key={i}>
                    <td>{req.vendorName}</td>
                    <td>{req.documentDescription}</td>
                    <td>{moment(req.workflowCreateDate).format('MM/DD/YYYY')}</td>
                    <td className={statusClass}>{req.workflowDocumentStatusCode}</td>
                    <td><span className="more glyphicon glyphicon-menu-down"></span></td>
                </tr>
            )
        })
        return (
            <div className="widget">
                <h2>
                    <span className="glyphicon glyphicon-usd"></span>
                    <span>My Orders</span>
                </h2>
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Vendor</th>
                                <th>Description</th>
                                <th>Date Created</th>
                                <th>Status</th>
                                <th>More</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows}
                        </tbody>
                    </table>
                    <button type="button" className="btn btn-default">Shop Catalogs</button>
                    <button type="button" className="btn btn-default">Create an Order</button>
                </div>
            </div>
        )
    }
});

export default MyOrders;