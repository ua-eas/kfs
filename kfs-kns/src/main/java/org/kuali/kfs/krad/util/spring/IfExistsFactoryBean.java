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
package org.kuali.kfs.krad.util.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AbstractFactoryBean;


/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 */
public class IfExistsFactoryBean extends AbstractFactoryBean implements BeanFactoryAware {
    private BeanFactory beanFactory;
    private String beanName;
    private Object customReturnValue;

    @Override
    protected Object createInstance() throws Exception {
        if (beanFactory.containsBean(beanName)) {
            if (customReturnValue != null) {
                return customReturnValue;
            } else {
                return beanFactory.getBean(beanName);
            }
        }
        return null;
    }

    /**
     * This overridden method ...
     *
     * @see org.springframework.beans.factory.config.AbstractFactoryBean#getObjectType()
     */
    @Override
    public Class getObjectType() {
        return null;
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Object getCustomReturnValue() {
        return this.customReturnValue;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setCustomReturnValue(Object customReturnValue) {
        this.customReturnValue = customReturnValue;
    }

}
