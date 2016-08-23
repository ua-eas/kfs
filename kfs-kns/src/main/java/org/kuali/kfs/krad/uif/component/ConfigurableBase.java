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
package org.kuali.kfs.krad.uif.component;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of <code>Configurable</code> that contains a Map<String, String> for holding
 * property expressions
 *
 * <p>
 * Should be extended by other UIF classes (such as <code>Component</code> or <code>LayoutManager</code>) to
 * provide property expression support
 * </p>
 *
 *
 */
public abstract class ConfigurableBase implements Configurable {
    private Map<String, String> propertyExpressions;

    public ConfigurableBase() {
        propertyExpressions = new HashMap<String, String>();
    }

    /**
     * @see ConfigurableBase#getPropertyExpressions
     */
    public Map<String, String> getPropertyExpressions() {
        return propertyExpressions;
    }

    /**
     * @see ConfigurableBase#setPropertyExpressions
     */
    public void setPropertyExpressions(Map<String, String> propertyExpressions) {
        this.propertyExpressions = propertyExpressions;
    }

    /**
     * @see ConfigurableBase#getPropertyExpression
     */
    public String getPropertyExpression(String propertyName) {
        if (this.propertyExpressions.containsKey(propertyName)) {
            return this.propertyExpressions.get(propertyName);
        }

        return null;
    }
}
