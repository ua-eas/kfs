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
package org.kuali.kfs.krad.uif.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Spring <code>BeanPostProcessor</code> that processes configured <code>Component</code>
 * instances in the dictionary
 */
public class ComponentBeanPostProcessor implements BeanPostProcessor {

    public ComponentBeanPostProcessor() {
    }

    /**
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * Sets the unique Id for a <code>Component</code> if bean name given (not generated) and the id property was
     * not set for the view
     * <p>
     * <p>
     * The ID will only be set here if an id is given for the Spring bean. For inner beans, the ID will be generated
     * during the view lifecycle
     * </p>
     *
     * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object,
     * java.lang.String)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Component) {
            Component component = (Component) bean;

            if (StringUtils.isBlank(component.getId())) {
                if (!StringUtils.contains(beanName, "$") && !StringUtils.contains(beanName, "#")) {
                    component.setId(beanName);
                    component.setFactoryId(beanName);
                }
            }
        }

        return bean;
    }
}
