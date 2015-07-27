function getUrlPathPrefix() {
    var path = new URL(window.location.href).pathname
    var pathPrefix = path.match(/^\/[^\/]+\//);
    return pathPrefix[0];
}

module.exports = {getUrlPathPrefix: getUrlPathPrefix}
