import React from 'react';

var Link = React.createClass({
    render: function() {
        return (
            <a href={this.props.url} className={this.props.className} target={this.props.target}>{this.props.label}</a>
        )
    }
});

export default Link
