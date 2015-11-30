import KfsUtils from './utils.js';

function getPrincipalName(success,fail) {
    $.ajax({
        url: KfsUtils.getUrlPathPrefix() + "sys/authentication/id",
        dataType: 'json',
        type: 'GET',
        success: function (principalName) {
            success(principalName.principalName);
        },
        error: function (xhr, status, err) {
            fail(err.toString());
        }
    })
}

function getUserPreferences(success,fail) {
    let p1 = new Promise ( getPrincipalName );

    p1.then(function(principalName) {
        $.ajax({
            url: KfsUtils.getUrlPathPrefix() + "sys/preferences/users/" + principalName,
            dataType: 'json',
            type: 'GET',
            success: function (userPreferences) {
                success(userPreferences);
            },
            error: function (xhr, status, err) {
                fail(err.toString());
            }
        });
    }).catch(function(message) {
        fail(message);
    });
 }

function putUserPreferences(userPreferences) {
    let p1 = new Promise ( getPrincipalName );

    p1.then(function(principalName) {
        $.ajax({
            url: KfsUtils.getUrlPathPrefix() + "sys/preferences/users/" + principalName,
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(userPreferences),
            type: 'PUT',
            error: function (xhr, status, err) {
                console.error(status, err.toString());
            }
        });
    }).catch(function(message) {
        console.log("Error: " + message);
    });
}

const UserPrefs = {
    getPrincipalName: getPrincipalName,
    getUserPreferences: getUserPreferences,
    putUserPreferences: putUserPreferences
}

module.exports = UserPrefs;
export default UserPrefs;
