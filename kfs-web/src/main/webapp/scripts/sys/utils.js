/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
