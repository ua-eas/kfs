/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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

import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * This is the contract for a Parameter.  The concept of a parameter is a key=value pair that is associated
 * with a rice enabled application.
 * <p>
 * <p>
 * When thinking of a parameter in terms of a key=value pair, the following defines the key and value parts:
 * <p>
 * the key of a parameter consists of the following pieces:
 * <p>
 * <ol>
 * <li>{@link #getApplicationId() applicationId}</li>
 * <li>{@link #getNamespaceCode() namespaceCode}</li>
 * <li>{@link #getComponentCode() componentCode}</li>
 * <li>{@link #getName() name}</li>
 * </ol>
 * <p>
 * the value consists of the {@link #getValue() value}
 * </p>
 */
public interface ParameterContract extends Versioned, GloballyUnique {

    /**
     * This is the application id for the Parameter.  This cannot be null or a blank string.
     * <p>
     * <p>
     * It is a way of assigning the Parameter to a specific rice application or rice ecosystem.
     * </p>
     *
     * @return application id
     */
    String getApplicationId();

    /**
     * This is the namespace for the parameter.  This cannot be null or a blank string.
     * <p>
     * <p>
     * It is a way of assigning the parameter to a logical grouping within a rice application or rice ecosystem.
     * </p>
     *
     * @return namespace code
     */
    String getNamespaceCode();

    /**
     * This is the component code for the parameter.  This cannot be null.
     * <p>
     * <p>
     * It is a way of assigning a parameter to a functional component within a rice application or rice ecosystem.
     * </p>
     *
     * @return component
     */
    String getComponentCode();

    /**
     * The name of the parameter.  This cannot be null or a blank string.
     *
     * @return name
     */
    String getName();

    /**
     * The value of the parameter.  This can be null or a blank string.
     *
     * @return value
     */
    String getValue();

    /**
     * This is the description for what the parameter is used for.  This can be null or a blank string.
     *
     * @return description
     */
    String getDescription();

    /**
     * This is the evaluation operator for the parameter.  This can be null.
     * <p>
     * <p>
     * This allows parameters to be used as primitive business rules.
     * </p>
     *
     * @return evaluation operator
     */
    EvaluationOperator getEvaluationOperator();

    /**
     * This is the type for the parameter.  This cannot be null.
     * <p>
     * <p>
     * Some parameters have special types in rice which may have special meaning
     * and is related to the {@link #getEvaluationOperator()}
     * </p>
     *
     * @return type
     */
    ParameterTypeContract getParameterType();

}
