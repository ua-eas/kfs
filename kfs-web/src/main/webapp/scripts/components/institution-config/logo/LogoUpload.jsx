import React from 'react/addons';


let LogoUpload = React.createClass({
    render() {
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1><span className="glyphicon glyphicon-cog"></span>Logo Upload</h1>
                </div>

                <div className="logo-upload main">
                    If you would like to brand Kuali Financials for your institution, we recommend that you upload an image that contains your institution's insignia (left) and name (right). We require that the image you upload be at least 62 pixels tall and have a transparent background.
                </div>
            </div>
        )
    }
});

export default LogoUpload;