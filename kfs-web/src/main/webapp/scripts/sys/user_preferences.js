/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
import KfsUtils from "./utils.js";

export function getPrincipalName(success, fail) {
    KfsUtils.ajaxCall({
        url: KfsUtils.getUrlPathPrefix() + "sys/api/v1/authentication/id",
        dataType: 'json',
        cache: false,
        type: 'GET',
        success: function (principalName) {
            success(principalName.principalName);
        },
        error: function (xhr, status, err) {
            fail(err.toString());
        }
    })
}

export function getUserPreferences(success, fail) {
    let p1 = new Promise(getPrincipalName);

    p1.then(function (principalName) {
        KfsUtils.ajaxCall({
            url: KfsUtils.getUrlPathPrefix() + "sys/api/v1/preferences/users/" + principalName,
            dataType: 'json',
            cache: false,
            type: 'GET',
            success: function (userPreferences) {
                success(userPreferences);
            },
            error: function (xhr, status, err) {
                fail(err.toString());
            }
        });
    }).catch(function (message) {
        fail(message);
    });
}

export function putUserPreferences(userPreferences) {
    let p1 = new Promise(getPrincipalName);

    p1.then(function (principalName) {
        KfsUtils.ajaxCall({
            url: KfsUtils.getUrlPathPrefix() + "sys/api/v1/preferences/users/" + principalName,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(userPreferences),
            cache: false,
            type: 'PUT',
            error: function (xhr, status, err) {
                console.error(status, err.toString());
            }
        });
    }).catch(function (message) {
        console.log("Error: " + message);
    });
}

export function getBackdoorId(success, fail) {
    let backdoorIdPath = KfsUtils.getUrlPathPrefix() + "sys/api/v1/backdoor/id";
    KfsUtils.ajaxCall({
        url: backdoorIdPath,
        dataType: 'json',
        cache: false,
        type: 'GET',
        success(backdoorIdJson) {
            success(backdoorIdJson['backdoorId']);
        },
        error(xhr, status, err) {
            fail(status, err);
        }
    })
}

const UserPrefs = {
    getPrincipalName,
    getUserPreferences,
    putUserPreferences,
    getBackdoorId
}

export default UserPrefs;
