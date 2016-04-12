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
package org.kuali.kfs.krad.datadictionary.validation.constraint.provider;

import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.resolver.ConstraintResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that implements a simple in memory storage map of constraint resolvers. This provides a convenient base class
 * from which other constraint providers can be derived. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @since 1.1
 */
public abstract class BaseConstraintProvider<T extends Constrainable> implements ConstraintProvider<T> {
	
	
	protected Map<String, ConstraintResolver<T>> resolverMap;
	
	public void init() {
		if (resolverMap == null)
			resolverMap = new HashMap<String, ConstraintResolver<T>>();

	}
	
	/**
	 * @see ConstraintProvider#getConstraints(Constrainable, java.lang.Class)
	 */
	@Override
	public List<Constraint> getConstraints(T definition, Class<? extends Constraint> constraintType) {
		if (resolverMap == null)
			init();
		
		ConstraintResolver<T> resolver = resolverMap.get(constraintType.getName());

		if (resolver == null)
			return null;
		
		return resolver.resolve(definition);
	}

	/**
	 * @return the resolverMap
	 */
	public Map<String, ConstraintResolver<T>> getResolverMap() {
		return this.resolverMap;
	}

	/**
	 * @param resolverMap the resolverMap to set
	 */
	public void setResolverMap(Map<String, ConstraintResolver<T>> resolverMap) {
		this.resolverMap = resolverMap;
	}
	
}
