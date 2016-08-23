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
package org.kuali.kfs.krad.util;

import org.kuali.kfs.krad.service.PersistenceStructureService;

import java.io.Serializable;
import java.util.List;

/**
 * This class is a token-style class, that is write-once, then read-only for all consumers of the class. It is often used as a
 * return value from various PersistenceStructureService methods.
 *
 * The object represents the state of the foreign-key fields of a reference object. For example, if Account is the bo, and
 * organization is the reference object, then chartOfAccountsCode and organizationCode are the foreign key fields. Their state,
 * rather they are all filled out, whether any of them are filled out, and which ones are not filled out, is what this class
 * represents.
 *
 *
 */
public class ForeignKeyFieldsPopulationState implements Serializable {

    private boolean allFieldsPopulated;
    private boolean anyFieldsPopulated;
    private List<String> unpopulatedFieldNames;

    public ForeignKeyFieldsPopulationState(boolean allFieldsPopulated, boolean anyFieldsPopulated, List<String> unpopulatedFieldNames) {
        this.allFieldsPopulated = allFieldsPopulated;
        this.anyFieldsPopulated = anyFieldsPopulated;
        this.unpopulatedFieldNames = unpopulatedFieldNames;
    }

    /**
     * Gets the allFieldsPopulated attribute.
     *
     * @return Returns the allFieldsPopulated.
     */
    public boolean isAllFieldsPopulated() {
        return allFieldsPopulated;
    }

    /**
     * Gets the anyFieldsPopulated attribute.
     *
     * @return Returns the anyFieldsPopulated.
     */
    public boolean isAnyFieldsPopulated() {
        return anyFieldsPopulated;
    }

    /**
     * Gets the unpopulatedFieldNames attribute.
     *
     * @return Returns the unpopulatedFieldNames.
     */
    public List<String> getUnpopulatedFieldNames() {
        return unpopulatedFieldNames;
    }

    /**
     * @see PersistenceStructureService.ForeignKeyFieldsPopulation#hasUnpopulatedFieldName(java.lang.String)
     */
    public boolean hasUnpopulatedFieldName(String fieldName) {
        if (this.unpopulatedFieldNames.contains(fieldName)) {
            return true;
        }
        else {
            return false;
        }
    }
}
