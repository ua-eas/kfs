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
package org.kuali.kfs.kns.datadictionary.control;

import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinition;
import org.kuali.kfs.krad.datadictionary.exception.CompletionException;

/**
 * Base class for control which provide a list of values to choose between.
 */
@Deprecated
public abstract class MultivalueControlDefinitionBase extends ControlDefinitionBase {
    private static final long serialVersionUID = -9164657952021540261L;

    /**
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        super.completeValidation(rootBusinessObjectClass, otherBusinessObjectClass);

        String valuesFinder = getValuesFinderClass();
        if (valuesFinder == null) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: keyValuesFinder was never set (" + "" + ")");
        }
    }
}
