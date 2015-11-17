import React from 'react/addons';
import Immutable from 'immutable';
import KfsUtils from '../../../sys/utils.js';
import Dropzone from 'react-dropzone';


let LogoUpload = React.createClass({
    getInitialState() {
        return {
            logo: new Immutable.Map(),
            logo: '',
            hasChanges: false,
            saveButtonText: 'SAVE CHANGES'
        };
    },
    componentWillMount() {
        let logoPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
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
        //data.append('test', 'a test string');

        let logoPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
            contentType: false,
            processData: false,
            method: 'POST',
            data: data,
            success: function() {
                this.setState({hasChanges: true, logo: files[0]}); // TODO -> make this the path in response
            }.bind(this),
            error: function(xhr, status, err) {
                let message = err ? 'Upload failed: ' + err : 'Upload failed.';
                $.notify(message, 'error');
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    saveChanges() {
        let logoPath = KfsUtils.getUrlPathPrefix() + "sys/preferences/config/logo";
        $.ajax({
            url: logoPath,
            dataType: 'json',
            contentType: 'application/json',
            method: 'PUT',
            data: JSON.stringify(this.state.logo),
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
                let message = err ? 'Save failed: ' + err : 'Save failed.';
                $.notify(message, 'error');
                console.error(status, err.toString());
            }.bind(this)
        });
    },
    render() {
        let logo = this.state.logo;
        let logoUrl;
        // TODO -> get rid of preview check
        if (logo && logo.preview) {
            logoUrl = logo.preview;
        } else if (logo && !logo.startsWith('http')) {
            logoUrl = KfsUtils.getUrlPathPrefix() + logo;
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
                        image you upload be at least 70 pixels tall and have a transparent background.
                    </div>

                    <div className="dropzone">
                        <Dropzone onDrop={this.onDrop} multiple={false}>
                            <div>Drag &amp; drop logo here or click to select a logo to upload.</div>
                        </Dropzone>
                    </div>

                    <div className="buttonbar">
                        <button disabled={saveDisabled} className={saveButtonClass} onClick={this.saveChanges}>{saveButtonText}</button>
                    </div>
                </div>
            </div>
        )
    }
});

export default LogoUpload;