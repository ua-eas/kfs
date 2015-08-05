var Footer = React.createClass({
    render() {
        var curYear = new Date().getFullYear();
        return (
            <div>
                <div className="center">Copyright 2005-{curYear} Kuali, Inc. All rights reserved.</div>
                <div className="center">Portions of this software are copyrighted by other parties as described in the <a target="_ack" href="acknowledgements.jsp">Acknowledgments</a> screen.</div>
            </div>
        );
    }
});

React.render(
    <Footer/>,
    document.getElementById('footer')
);

export default Footer;
