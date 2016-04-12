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
package org.kuali.kfs.krad.uif.control;

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UifKeyValuesFinderBase extends KeyValuesBase implements UifKeyValuesFinder {

    private boolean addBlankOption;

    public UifKeyValuesFinderBase() {
        addBlankOption = true;
    }

    /**
     * @see UifKeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return null;
    }

    /**
     * @see UifKeyValuesFinder#isAddBlankOption()
     */
    public boolean isAddBlankOption() {
        return addBlankOption;
    }

    /**
     * Setter for the addBlankOption indicator
     *
     * @param addBlankOption
     */
    public void setAddBlankOption(boolean addBlankOption) {
        this.addBlankOption = addBlankOption;
    }
}
