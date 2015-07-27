function getUrlPathPrefix() {
    var path = URL(window.location.href).pathname
    var pathPrefix = path.match(/^[^\/]+\/\/[^\/]+\/[^\/]+\//);
    return pathPrefix[1];
}
