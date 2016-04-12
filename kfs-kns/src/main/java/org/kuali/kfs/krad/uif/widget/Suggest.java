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
package org.kuali.kfs.krad.uif.widget;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.field.AttributeQuery;

/**
 * Widget that provides dynamic select options to the user as they
 * are entering the value (also known as auto-complete)
 *
 * <p>
 * Widget is backed by an <code>AttributeQuery</code> that provides
 * the configuration for executing a query server side that will retrieve
 * the valid option values.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class Suggest extends WidgetBase {
    private static final long serialVersionUID = 7373706855319347225L;

    private AttributeQuery suggestQuery;
    private String sourcePropertyName;

    public Suggest() {
        super();
    }

    /**
     * The following actions are performed:
     *
     * <ul>
     * <li>Adjusts the query field mappings on the query based on the binding configuration of the field</li>
     * <li>TODO: determine query if render is true and query is not set</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     *      java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // if source property name set then can't render the Suggest widget
        if (StringUtils.isBlank(sourcePropertyName)) {
            setRender(false);
        }

        if (!isRender()) {
            return;
        }

        InputField field = (InputField) parent;
        BindingInfo bindingInfo = field.getBindingInfo();

        // adjust from side on query field mapping to match parent fields path
        suggestQuery.updateQueryFieldMapping(bindingInfo);
    }

    /**
     * Attribute query instance the will be executed to provide
     * the suggest options
     *
     * @return AttributeQuery
     */
    public AttributeQuery getSuggestQuery() {
        return suggestQuery;
    }

    /**
     * Setter for the suggest attribute query
     *
     * @param suggestQuery
     */
    public void setSuggestQuery(AttributeQuery suggestQuery) {
        this.suggestQuery = suggestQuery;
    }

    /**
     * Name of the property on the query result object that provides
     * the options for the suggest, values from this field will be
     * collected and sent back on the result to provide as suggest options
     *
     * @return String source property name
     */
    public String getSourcePropertyName() {
        return sourcePropertyName;
    }

    /**
     * Setter for the source property name
     *
     * @param sourcePropertyName
     */
    public void setSourcePropertyName(String sourcePropertyName) {
        this.sourcePropertyName = sourcePropertyName;
    }
}
