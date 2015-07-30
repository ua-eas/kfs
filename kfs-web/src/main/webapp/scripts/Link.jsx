var Link = React.createClass({
    render: function() {
        return (
            <a href={this.props.url} className={this.props.className}>{this.props.label}</a>
        )
    }
});

export default Link