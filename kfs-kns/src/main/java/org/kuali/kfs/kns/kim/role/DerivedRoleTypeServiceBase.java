/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.kns.kim.role;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.role.RoleMembership;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is the base class for all derived role type services 
 *
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class DerivedRoleTypeServiceBase extends RoleTypeServiceBase {

	@Override
	public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (roleName == null) {
            throw new RiceIllegalArgumentException("roleName was null");
        }

        return Collections.emptyList();
	}

	/**
	 * @see RoleTypeServiceBase#isDerivedRoleType()
	 */
	@Override
	public boolean isDerivedRoleType() {
		return true;
	}

}
