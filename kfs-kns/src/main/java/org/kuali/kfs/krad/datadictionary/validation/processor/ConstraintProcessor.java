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
package org.kuali.kfs.krad.datadictionary.validation.processor;


import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;

/**
 * This interface must be implemented by constraint processors, which validate individual constraints in the
 * data dictionary. The idea is that each constraint has its own processor, and that the validation service can be configured
 * via dependency injection with a list of processors. This gives institutions the ability to easily modify how validation
 * should be handled and to add arbitrary new constraints and constraint processors. An alternative might have been to put
 * the process() method into the Constraint marker interface and have each Constraint define its own processing, but that would
 * have forced business logic into what are naturally API classes (classes that implement Constraint). This strategy separates
 * the two functions.
 *
 *
 */
public interface ConstraintProcessor<T, C extends Constraint> {

	public ProcessorResult process(DictionaryValidationResult result, T value, C constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException;

	public String getName();

	public Class<? extends Constraint> getConstraintType();

	/**
	 * This method return true if the processing of this constraint is something that can be opted out of by some pieces of code.
	 * The only example of this in the version under development (1.1) is the existence constraint.
	 *
	 * @return true if this processor can be turned off by some pieces of code, false otherwise
	 */
	public boolean isOptional();

}
