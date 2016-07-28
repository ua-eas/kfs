import URL from 'url';
import Cookie from 'js-cookie';

export function getKualiSessionId() {
    let kualiCookieRegex = new RegExp("kualiSessionId=([^;]+)");
    let value = kualiCookieRegex.exec(document.cookie);
    return (value != null) ? value[1] : null;
}

export function getUrlPathPrefix() {
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

export function buildBackdoorIdAppender(backdoorId) {
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

export function buildKeyFromLabel(label) {
    return label.toLowerCase().replace(/\s+/g, "-").replace("&","and");
}

export function ajaxCall(call) {
    const financialsAuthToken = Cookie.get('financialsAuthToken');
    if (financialsAuthToken) {
        let headers = (Object.keys(call).indexOf('headers') > -1)
            ? call.headers
            : {};
        headers['Authorization'] = 'bearer '+financialsAuthToken;
        call.headers = headers;
    }
    $.ajax(call);
}

const KfsUtils = {
    getKualiSessionId,
    getUrlPathPrefix,
    buildBackdoorIdAppender,
    buildKeyFromLabel,
    ajaxCall
};

export default KfsUtils;
