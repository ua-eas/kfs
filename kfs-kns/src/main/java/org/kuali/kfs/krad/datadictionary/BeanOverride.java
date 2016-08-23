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
package org.kuali.kfs.krad.datadictionary;

import java.util.List;

/**
 * Performs overrides on the fields of a Data Dictionary bean.
 *
 *
 *
 */
public interface BeanOverride {
	 /**
     * Return the name of the bean to perform the override.
     * @return
     */
    public String getBeanName();
    /**
     * Returns the list of fields to perform the override.
     */
    public List<FieldOverride> getFieldOverrides();

    /**
     * Perform the override logic on the specific bean.
     */
    public void performOverride(Object bean);
}
