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

import org.kuali.kfs.krad.datadictionary.exception.CompletionException;

import java.io.Serializable;

/**
 * Defines methods common to all DataDictionaryDefinition types.
 */
public interface DataDictionaryDefinition extends Serializable {

    /**
     * Performs complete intra-definition validation which couldn't be done earlier - for example, verifies that field references
     * refer to actual fields of some specific class.
     *
     * @param rootBusinessObjectClass  Class of the BusinessObjectEntry which ultimately contains this definition
     * @param otherBusinessObjectClass other stuff required to complete validation
     * @throws CompletionException if a problem arises during validation-completion
     */
    public void completeValidation(Class<?> rootBusinessObjectClass, Class<?> otherBusinessObjectClass);

    public String getId();
}
