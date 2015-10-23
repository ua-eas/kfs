import URL from 'url'

function getKualiSessionId() {
    let kualiCookieRegex = new RegExp("kualiSessionId=([^;]+)");
    let value = kualiCookieRegex.exec(document.cookie);
    return (value != null) ? value[1] : null;
}

function getUrlPathPrefix() {
    let path = URL.parse(window.location.href).pathname;
    let pathPrefix = path.match(/^\/[^\/]+\//);
    return pathPrefix[0];
}

function reconstructQueryWithBackdoorId(query, backdoorId) {
    let prefixElements = [];
    let suffixElements = [];

    Object.keys(query).forEach((key) => {
        let val = query[key];
        if (val) {
            if (val.indexOf("http") >= 0) {
                suffixElements.push(key + "=" + val);
            } else {
                prefixElements.push(key + "=" + val);
            }
        } else {
            prefixElements.push(key);
        }
    });
    let reconstructedQuery = "?" + (prefixElements.length > 0 ? prefixElements.join("&") + "&" : "") + "backdoorId=" + backdoorId + (suffixElements.length > 0 ? "&"+suffixElements.join("&") : "");
    return reconstructedQuery;
}

function buildBackdoorIdAppender(backdoorId) {
    if (!backdoorId || backdoorId.length == 0) {
        return function(link) {
            return link;
        }
    }
    return function(link) {
        if (link && link.length > 0) {
            let linkUrl = URL.parse(link, true);
            if (linkUrl.query && Object.keys(linkUrl.query).length > 0) {
                return linkUrl.protocol + "//" + linkUrl.host + linkUrl.pathname + reconstructQueryWithBackdoorId(linkUrl.query, backdoorId);
            }
            return link + "?backdoorId=" + backdoorId;
        }
        return link;
    }
}

function buildKeyFromLabel(label) {
    return label.toLowerCase().replace(/\s+/g, "-").replace("&","and");
}

module.exports = {
    getKualiSessionId: getKualiSessionId,
    getUrlPathPrefix: getUrlPathPrefix,
    buildBackdoorIdAppender: buildBackdoorIdAppender,
    buildKeyFromLabel: buildKeyFromLabel
};

