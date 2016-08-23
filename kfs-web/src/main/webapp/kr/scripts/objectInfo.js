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
function loadUserInfo( userIdFieldName, universalIdFieldName, userNameFieldName ) {
	var userId = dwr.util.getValue( userIdFieldName );

	if (userId == "") {
		clearRecipients( universalIdFieldName, "" );
		clearRecipients( userNameFieldName, "" );
	} else {
		var dwrReply = {
			callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					if ( universalIdFieldName != null && universalIdFieldName != "" ) {
						setRecipientValue( universalIdFieldName, data.principalId );
					}
					if ( userNameFieldName != null && userNameFieldName != "" ) {
						setRecipientValue( userNameFieldName, data.name );
					} else {
						// guess the DIV name
						divName = userIdFieldName.replace( ".principalName", ".name.div" );
						dwr.util.setValue( divName, data.name );
					}
				} else {
					if ( universalIdFieldName != null && universalIdFieldName != "" ) {
						setRecipientValue( universalIdFieldName, "" );
					}
					if ( userNameFieldName != null && userNameFieldName != "" ) {
						setRecipientValue( userNameFieldName, wrapError( "person not found" ), true );
					} else {
						// guess the DIV name
						divName = userIdFieldName.replace( ".principalName", ".name.div" );
						dwr.util.setValue( divName, wrapError( "person not found" ), { escapeHtml:false} );
					}
				}
			},
			errorHandler:function( errorMessage ) {
				window.status = errorMessage;
				if ( universalIdFieldName != null && universalIdFieldName != "" ) {
					setRecipientValue( universalIdFieldName, "" );
				}
				if ( userNameFieldName != null && userNameFieldName != "" ) {
					setRecipientValue( userNameFieldName, wrapError( "person not found" ), true );
				} else {
					// guess the DIV name
					divName = userIdFieldName.replace( ".principalName", ".name.div" );
					dwr.util.setValue( divName, wrapError( "person not found" ), { escapeHtml:false} );
				}
			}
		};
		PersonService.getPersonByPrincipalName( userId, dwrReply );
	}
}

