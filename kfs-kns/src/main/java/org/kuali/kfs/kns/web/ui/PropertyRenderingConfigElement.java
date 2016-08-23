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
package org.kuali.kfs.kns.web.ui;

import org.kuali.rice.core.web.format.Formatter;

/**
 * Interface to be implemented by user interface elements that hold configuration about rendering a
 * property
 */
@Deprecated
public interface PropertyRenderingConfigElement {

    /**
     * @return name of the property that is to be rendered
     */
    public String getPropertyName();

    /**
     * @param propertyName - name of the property that is to be rendered
     */
    public void setPropertyName(String propertyName);

    /**
     * @return name of the property that is to be rendered
     */
    public String getPropertyValue();

    /**
     * @param propertyValue - value of the property that is to be rendered
     */
    public void setPropertyValue(String propertyValue);

    /**
     * @return value of the property that is to be rendered
     */
    public Formatter getFormatter();

    /**
     * @param formatter - {@link Formatter} that should be use when rendering property value
     */
    public void setFormatter(Formatter formatter);

    /**
     * @return name of the property that should be displayed in place of property we are rendering
     * (only applies when read-only)
     */
    public String getAlternateDisplayPropertyName();

    /**
     * @param alternateDisplayPropertyName - name of the property that should be displayed in place of property we are
     *                                     rendering (only applies when read-only)
     */
    public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName);

    /**
     * @return name of the property that should be displayed in addition to the property we are
     * rendering (only applies when read-only)
     */
    public String getAdditionalDisplayPropertyName();

    /**
     * @param additionalDisplayPropertyName - name of the property that should be displayed in addition to the property we are
     *                                      rendering (only applies when read-only)
     */
    public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName);

}
