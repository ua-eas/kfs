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
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.workflow.DataDictionaryPeopleFlowTypeServiceImpl;
import org.kuali.rice.core.api.uif.RemotableAttributeField;

/**
 * Provides service methods for building and validate {@link RemotableAttributeField} definitions from data
 * dictionary {@link AttributeDefinition} configurations
 * <p>
 * <p>
 * Used by the default type services {@link DataDictionaryPeopleFlowTypeServiceImpl} to
 * build the remotable fields for the type attributes
 * </p>
 */
public interface DataDictionaryRemoteFieldService {

    /**
     * Builds and returns an {@link RemotableAttributeField} instance based on the data dictionary attribute definition
     * that is associated with the given component class name (business object or data object entry) and the given
     * attribute name
     * <p>
     * <p>
     * If an attribute definition is not found a runtime exception should be thrown
     * </p>
     *
     * @param componentClassName - class name for the attribute, used to find the data dictionary entry
     * @param attributeName      - name of the attribute whose definition should be used
     * @return RemotableAttributeField instance built
     */
    public RemotableAttributeField buildRemotableFieldFromAttributeDefinition(String componentClassName,
                                                                              String attributeName);

}
