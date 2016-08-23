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
package org.kuali.kfs.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.config.property.ConfigContext;

/**
 * A custom MessageResourceFactory that delegates to the ConfigurationService's pre-loaded properties.
 * <p>
 * This factory can be used in struts-config.xml files by specifying a factory attribute in the <message-resources/> tag.
 * Example:
 * <message-resources
 * factory="KualiPropertyMessageResourcesFactory"
 * parameter="SampleApplicationResources" />
 */
public class KualiPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory {

    private static final long serialVersionUID = 9045578011738154255L;

    /**
     * Uses KualiPropertyMessageResources, which allows multiple property files to be loaded into the defalt message set.
     *
     * @see org.apache.struts.util.MessageResourcesFactory#createResources(java.lang.String)
     */
    @Override
    public MessageResources createResources(String config) {
        if (StringUtils.isBlank(config)) {
            final String propertyConfig = (String) ConfigContext.getCurrentContextConfig().getProperties().get(
                KRADConstants.MESSAGE_RESOURCES);
            config = removeSpacesAround(propertyConfig);
        }
        return new KualiPropertyMessageResources(this, config, this.returnNull);
    }

    /**
     * Removes the spaces around the elements on a csv list of elements.
     * <p>
     * A null input will return a null output.
     * </p>
     *
     * @param csv a list of elements in csv format e.g. foo, bar, baz
     * @return a list of elements in csv format without spaces e.g. foo,bar,baz
     */
    private String removeSpacesAround(String csv) {
        if (csv == null) {
            return null;
        }

        final StringBuilder result = new StringBuilder();
        for (final String value : csv.split(",")) {
            if (!"".equals(value.trim())) {
                result.append(value.trim());
                result.append(",");
            }
        }

        //remove trailing comma
        int i = result.lastIndexOf(",");
        if (i != -1) {
            result.deleteCharAt(i);
        }

        return result.toString();
    }

}
