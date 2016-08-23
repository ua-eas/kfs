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
package org.kuali.kfs.krad.datadictionary.validation.result;

import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 */
public class ProcessorResult {

	private final transient List<Constraint> constraints;
	private final transient Constrainable definition;
	private final transient AttributeValueReader attributeValueReader;

	private final List<ConstraintValidationResult> constraintValidationResults;

	public ProcessorResult(ConstraintValidationResult constraintValidationResult) {
		this(constraintValidationResult, null, null);
	}

	public ProcessorResult(ConstraintValidationResult constraintValidationResult, Constrainable definition, AttributeValueReader attributeValueReader, Constraint... constraints) {
		this.constraintValidationResults = Collections.singletonList(constraintValidationResult);
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
		this.constraints = Arrays.asList(constraints);
	}

    public ProcessorResult(ConstraintValidationResult constraintValidationResult, Constrainable definition, AttributeValueReader attributeValueReader, List<Constraint> constraints) {
        this.constraintValidationResults = Collections.singletonList(constraintValidationResult);
        this.definition = definition;
        this.attributeValueReader = attributeValueReader;
        this.constraints = constraints;
    }

	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults) {
		this(constraintValidationResults, null, null);
	}

	public ProcessorResult(List<ConstraintValidationResult> constraintValidationResults, Constrainable definition, AttributeValueReader attributeValueReader, Constraint... constraints) {
		this.constraintValidationResults = constraintValidationResults;
		this.definition = definition;
		this.attributeValueReader = attributeValueReader;
		this.constraints = Arrays.asList(constraints);
	}

	public boolean isSingleConstraintResult() {
		return this.constraintValidationResults.size() == 1;
	}

	public boolean isDefinitionProvided() {
		return definition != null;
	}

	public boolean isAttributeValueReaderProvided() {
		return attributeValueReader != null;
	}

	public ConstraintValidationResult getFirstConstraintValidationResult() {
		return this.constraintValidationResults.isEmpty() ? null : this.constraintValidationResults.get(0);
	}

	/**
	 * @return the constraintValidationResults
	 */
	public List<ConstraintValidationResult> getConstraintValidationResults() {
		return this.constraintValidationResults;
	}

	/**
	 * @return the definition
	 */
	public Constrainable getDefinition() {
		return this.definition;
	}

	/**
	 * @return the attributeValueReader
	 */
	public AttributeValueReader getAttributeValueReader() {
		return this.attributeValueReader;
	}

	/**
	 * @return the constraints
	 */
	public List<Constraint> getConstraints() {
		return this.constraints;
	}

}
