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
package org.kuali.kfs.coreservice.framework.parameter;

import org.kuali.kfs.coreservice.api.parameter.EvaluationOperator;
import org.kuali.kfs.coreservice.api.parameter.ParameterContract;
import org.kuali.kfs.coreservice.api.parameter.ParameterTypeContract;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * TODO: Likely should remove all methods from this interface after KULRICE-7170 is fixed
 */
public interface ParameterEbo extends ParameterContract,
		ExternalizableBusinessObject {

	/**
     * This is the application id for the Parameter.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the Parameter to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application id
     */
	String getApplicationId();

    /**
     * This is the namespace for the parameter.  This cannot be null or a blank string.
     *
     * <p>
     * It is a way of assigning the parameter to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
	String getNamespaceCode();

	/**
     * This is the component code for the parameter.  This cannot be null.
     *
     * <p>
     * It is a way of assigning a parameter to a functional component within a rice application or rice ecosystem.
     * </p>
     *
     * @return component
     */
	String getComponentCode();

    /**
     * The name of the parameter.  This cannot be null or a blank string.
     * @return name
     */
    String getName();

    /**
     * The value of the parameter.  This can be null or a blank string.
     * @return value
     */
	String getValue();

    /**
     * This is the description for what the parameter is used for.  This can be null or a blank string.
     * @return description
     */
	String getDescription();

    /**
     * This is the evaluation operator for the parameter.  This can be null.
     *
     * <p>
     * This allows parameters to be used as primitive business rules.
     * </p>
     *
     * @return evaluation operator
     */
	EvaluationOperator getEvaluationOperator();

    /**
     * This is the type for the parameter.  This cannot be null.
     *
     * <p>
     * Some parameters have special types in rice which may have special meaning
     * and is related to the {@link #getEvaluationOperator()}
     * </p>
     *
     * @return type
     */
	ParameterTypeContract getParameterType();

	/**
	 * Returns the version number for this object.  In general, this value should only
	 * be null if the object has not yet been stored to a persistent data store.
	 * This version number is generally used for the purposes of optimistic locking.
	 *
	 * @return the version number, or null if one has not been assigned yet
	 */
	Long getVersionNumber();

	/**
	 * Return the globally unique object id of this object.  In general, this value should only
	 * be null if the object has not yet been stored to a persistent data store.
	 *
	 * @return the objectId of this object, or null if it has not been set yet
	 */
	String getObjectId();
}
