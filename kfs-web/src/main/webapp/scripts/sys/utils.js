import URL from 'url'

function getUrlPathPrefix() {
    let path = URL.parse(window.location.href).pathname
    let pathPrefix = path.match(/^\/[^\/]+\//);
    return pathPrefix[0];
}

class BackdoorIdAppender {
    constructor() {
        let url = URL.parse(window.location.href, true)
        let query = url.query
        this.backdoorId = query.backdoorId ? "backdoorId=" + query.backdoorId : ""
    }
    appendBackdoorId(link) {
        if (this.backdoorId.length > 0 && link && link.length > 0) {
            let linkUrl = URL.parse(link, false)
            if (linkUrl.query && linkUrl.query.length > 0) {
                return link + "&" + this.backdoorId
            }
            return link + "?" + this.backdoorId
        }
        return link
    }
}

module.exports = {getUrlPathPrefix: getUrlPathPrefix, BackdoorIdAppender: BackdoorIdAppender}
