import KfsUtils from './sys/utils.js';

var TestHeader = React.createClass({
    getInitialState() {
        return {environment: {}, backdoorId: ""}
    },
    componentWillMount() {
        let environmentPath = KfsUtils.getUrlPathPrefix() + "sys/system/environment"
        $.ajax({
            url: environmentPath,
            dataType: 'json',
            type: 'GET',
            success: function(env) {
                this.setState({environment: env});

                if (this.state.environment && this.state.environment.prodMode === false) {
                    $('.body').addClass('test-env')
                    $('#test-header').css('display', 'block')
                }
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })

        let backdoorPath = KfsUtils.getUrlPathPrefix() + "sys/backdoor/id"
        $.ajax({
            url: backdoorPath,
            dataType: 'json',
            contentType: 'application/json',
            type: 'GET',
            success: function(backdoorJson) {
                this.setState({backdoorId: backdoorJson.backdoorId});
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    handleBackDoorEnter(e) {
        if (e.charCode == 13) {
            $('#backdoorLoginButton').click()
        }
    },
    backdoorLogin(event) {
        event.preventDefault();
        let path = KfsUtils.getUrlPathPrefix() + "sys/backdoor/login"
        console.log(path)
        let backdoorId = $("#backdoorId").val()
        if (backdoorId !== '') {
            $.ajax({
                url: path,
                dataType: 'json',
                contentType: 'application/json',
                data: JSON.stringify({backdoorId: backdoorId}),
                type: 'POST',
                success: function () {
                    $("#backdoorId").val("")
                    window.location.reload()
                }.bind(this),
                error: function (xhr, status, err) {
                    console.error(status, err.toString());
                }.bind(this)
            })
        }
    },
    backdoorLogout() {
        let path = KfsUtils.getUrlPathPrefix() + "sys/backdoor/logout"
        $.ajax({
            url: path,
            dataType: 'json',
            contentType: 'application/json',
            type: 'GET',
            success: function() {
                window.location.reload()
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        })
    },
    render() {
        let logoutButton;
        if (this.state.backdoorId && this.state.backdoorId !== '') {
            logoutButton = <button type="button" className="btn btn-default" onClick={this.backdoorLogout}>Logout</button>
        }

        if (this.state.environment && !this.state.environment.prodMode) {
            return (
                <div className="test-info">
                    <div className="column first">This is a test environment</div>
                    <div className="column middle">{this.state.backdoorId ? 'Impersonating: ' + this.state.backdoorId : ''}&nbsp;</div>
                    <div className="column last">
                        <input id="backdoorId" type="text" placeholder="Back Door ID" onKeyPress={this.handleBackDoorEnter}/>
                        <button type="button" id="backdoorLoginButton" className="btn btn-default" onClick={this.backdoorLogin}>Login</button>
                        {logoutButton}
                    </div>
                </div>
            )
        } else {
            return (<div></div>)
        }

    }
})

React.render(
    <TestHeader/>,
    document.getElementById('test-header')
);

export default TestHeader;