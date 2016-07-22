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
package org.kuali.kfs.krad.uif.component;

import java.util.Map;

/**
 * Marks any class that can be configured through the UIF dictionary
 *
 * <p>
 * Indicates behavior that must be supported by an Class that can be configured through
 * the UIF dictionary, such as property expressions.
 * </p>
 *
 * 
 */
public interface Configurable {

    /**
     * Map of expressions that should be evaluated to conditionally set a property on the component
     *
     * <p>
     * When configuring a component property through XML an expression can be given using the @{} placeholder. During
     * the loading of the XML any such expressions are captured and placed into this Map, with the property they apply
     * to set as the Map key. The expressions are then evaluated during the apply model phase and the result is set as
     * the property value.
     * </p>
     *
     * <p>
     * Note after the expression is picked up, the property configuration is removed. Thus the property in the
     * component will only have its default object value until the expression is evaluated
     * </p>
     *
     * @return Map<String, String> map of expressions where key is property name and value is expression to evaluate
     */
    public Map<String, String> getPropertyExpressions();

    /**
     * Setter for the Map of property expressions
     *
     * @param propertyExpressions
     */
    public void setPropertyExpressions(Map<String, String> propertyExpressions);

    /**
     * Returns the expression configured for the property with the given name
     *
     * @return String expression for property or null if expression is not configured
     * @see Component#getPropertyExpressions()
     */
    public String getPropertyExpression(String propertyName);
}
