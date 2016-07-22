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
package org.kuali.kfs.krad.datadictionary.impl;

import java.util.List;

import org.kuali.kfs.krad.datadictionary.FieldOverride;

/**
 * A Field Override used to replace list elements in a Data Dictionary bean. 
 * 
 * 
 * 
 */
public class FieldOverrideForListElementReplaceImpl extends FieldOverrideForListElementBase implements FieldOverride {
	private Object replaceWith;

	public Object getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(Object replaceAt) {
		this.replaceWith = replaceAt;
	}

	protected void varifyConfig() {
		if (replaceWith == null) {
			throw new RuntimeException(
					"Configuration Error, Missing required replaceWith parameter....");
		}
	}

	public Object performFieldOverride(Object bean, Object property) {
		varifyConfig();
		List oldList = (List) property;

		int replacePos = getElementPositionInList(getElement(), oldList);

		if (replacePos == -1) {
			throw new RuntimeException(
					"Configuration Error, replace element could not be located.");
		}
		if (replacePos >= 0 && replacePos < oldList.size()) {
			oldList.remove(replacePos);
			oldList.add(replacePos, getReplaceWith());
		}

		return oldList;
	}
}
