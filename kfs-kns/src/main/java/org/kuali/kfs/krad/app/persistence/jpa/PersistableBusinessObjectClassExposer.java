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
package org.kuali.kfs.krad.app.persistence.jpa;

import java.util.Set;

/**
 * Contract for classes which plan to expose class names to the RicePersistenceUnitPostProcessor, for
 * dynamic loading 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface PersistableBusinessObjectClassExposer {
	/**
	 * Exposes a Set of class names for PersistableBusinessObjects, mapped as JPA entities, which
	 * should be managed by the JPA persistable unit
	 * @return a Set of class names to be managed by JPA
	 */
	public abstract Set<String> exposePersistableBusinessObjectClassNames();
}
