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
package org.kuali.kfs.sys.service;

import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;

import java.util.List;

public interface KfsBusinessObjectMetaDataService {
    public BusinessObjectProperty getBusinessObjectProperty(String componentClass, String propertyName);

    public List<BusinessObjectComponent> findBusinessObjectComponents(String namespaceCode, String componentLabel);

    public List<BusinessObjectProperty> findBusinessObjectProperties(String namespaceCode, String componentLabel, String propertyLabel);

    public boolean isMatch(String componentClass, String propertyName, String tableNameSearchCriterion, String fieldNameSearchCriterion);

    public String getReferenceComponentLabel(Class componentClass, String propertyName);
}
