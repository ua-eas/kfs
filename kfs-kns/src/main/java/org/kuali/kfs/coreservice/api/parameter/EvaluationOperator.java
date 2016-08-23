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
package org.kuali.kfs.coreservice.api.parameter;

import org.kuali.rice.core.api.mo.common.Coded;
import org.kuali.rice.core.api.util.jaxb.EnumStringAdapter;

/**
 * Defines the possible evaluation operators that can be supported on system parameters.
 */
public enum EvaluationOperator implements Coded {

	/**
	 * Indicates that evaluation will determine if the value being tested in present
	 * in the set of values defined on the parameter.  If it is present in this set,
	 * then evaluation will succeed.
	 */
	ALLOW("A"),

	/**
	 * Indicates that evaluation will determine if the value being tested is absent
	 * from the set of values defined on the parameter.  If it is absent from this
	 * set, then the evaluation will succeed.
	 */
	DISALLOW("D");

	private final String code;

	EvaluationOperator(final String code) {
		this.code = code;
	}

	/**
	 * Returns the operator code for this evaluation operator.
	 *
	 * @return the code
	 */
	@Override
	public String getCode() {
		return code;
	}

	public static EvaluationOperator fromCode(String code) {
		if (code == null) {
			return null;
		}
		for (EvaluationOperator operator : values()) {
			if (operator.code.equals(code)) {
				return operator;
			}
		}
		throw new IllegalArgumentException("Failed to locate the EvaluationOperator with the given code: " + code);
	}

	static final class Adapter extends EnumStringAdapter<EvaluationOperator> {

		protected Class<EvaluationOperator> getEnumClass() {
			return EvaluationOperator.class;
		}

	}

}
