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
package org.kuali.kfs.krad.datadictionary.impl;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.datadictionary.BeanOverride;
import org.kuali.kfs.krad.datadictionary.FieldOverride;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * The base implementation of the BeanOverride interface.
 */
public class BeanOverrideImpl implements BeanOverride {
    private static final Logger LOG = Logger.getLogger(BeanOverrideImpl.class);
    private String beanName;
    private List<FieldOverride> fieldOverrides;

    /**
     * @see BeanOverride#getFieldOverrides()
     */
    public List<FieldOverride> getFieldOverrides() {
        return this.fieldOverrides;
    }

    public void setFieldOverrides(List<FieldOverride> fieldOverirdes) {
        this.fieldOverrides = fieldOverirdes;
    }

    /**
     * @see BeanOverride#getBeanName()
     */
    public String getBeanName() {
        return this.beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * @see BeanOverride#performOverride(java.lang.Object)
     */
    public void performOverride(Object bean) {
        try {
            for (FieldOverride fieldOverride : fieldOverrides) {
                Object property = PropertyUtils.getProperty(bean, fieldOverride.getPropertyName());
                Object newProperty = fieldOverride.performFieldOverride(bean, property);
                BeanUtils.setProperty(bean, fieldOverride.getPropertyName(), newProperty);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
