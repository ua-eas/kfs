import React from 'react';
import Immutable from 'immutable';
import KfsUtils from '../../../sys/utils.js';
import Dropzone from 'react-dropzone';


let LogoUpload = React.createClass({
    getInitialState() {
        return {
            logo: '',
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };
    },
    componentWillMount() {
        let logoPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
            cache: false,
            method: 'GET',
            success: function(logo) {
                this.setState({
                    logo: logo.logoUrl
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    onDrop: function (files) {
        let data = new FormData();
        data.append('logo', files[0]);

        let logoPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
            contentType: false,
            processData: false,
            cache: false,
            method: 'POST',
            data: data,
            success: function(logo) {
                this.setState({hasChanges: true, logo: logo.logoUrl, error: undefined});
            }.bind(this),
            error: function(xhr, status, err) {
                let message = xhr.responseText ? xhr.responseText : 'Upload failed.';
                $.notify(message, 'error');
                this.setState({'error': message});
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    saveChanges() {
        let data = {'logoUrl': this.state.logo};
        let logoPath = KfsUtils.getUrlPathPrefix() + "api/v1/sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
            contentType: 'application/json',
            cache: false,
            method: 'PUT',
            data: JSON.stringify(data),
            success: function() {
                let spanStyle = {
                    color: '#6DA487'
                };
                this.setState({
                    hasChanges: false,
                    saveButtonText: <span style={spanStyle}><span className="glyphicon glyphicon-ok"></span>SAVED</span>
                });
                $.notify('Save Successful!', 'success');
            }.bind(this),
            error: function(xhr, status, err) {
                let message = xhr.responseText ? xhr.responseText : 'Save failed.';
                $.notify(message, 'error');
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    render() {
        let logoUrl = this.state.logo;
        if (logoUrl && logoUrl.indexOf('data:') !== 0 && !logoUrl.startsWith('http')) {
            logoUrl = KfsUtils.getUrlPathPrefix() + logoUrl;
        }

        let saveDisabled;
        let saveButtonClass = 'btn btn-green';
        let saveButtonText = this.state.saveButtonText;
        if (!this.state.hasChanges) {
            saveDisabled = true;
            saveButtonClass += ' disabled';
        } else {
            saveButtonText = "SAVE CHANGES";
        }
        return (
            <div>
                <div className="headerarea-small" id="headerarea-small">
                    <h1>Logo Upload</h1>
                </div>

                <div className="logo-upload main">
                    <h4>Preview</h4>
                    <div className="preview-box">
                        <img src={logoUrl} height="35px"/>
                        <span className="logo-right">Financials</span>
                    </div>
                    <div className="instructions">
                        If you would like to brand Kuali Financials for your institution, we recommend that you upload
                        an image that contains your institution's insignia (left) and name (right). We require that the
                        image you upload be at least 70 pixels tall, be no larger than 100kb, and have a transparent background. 
                    </div>

                    <div className="dropzone">
                        <Dropzone onDrop={this.onDrop} multiple={false}>
                            <div>Drag &amp; drop logo here or click to select a logo to upload.</div>
                        </Dropzone>
                    </div>

                    <div className="error">{this.state.error}</div>

                    <div className="buttonbar">
                        <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                    </div>
                </div>
            </div>
        )
    }
});

export default LogoUpload;