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
package org.kuali.kfs.krad.app.persistence.jpa;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;


public class RiceEntityManagerProxyFactoryBean implements FactoryBean, InitializingBean {

    private RiceLocalContainerEntityManagerFactoryBean factoryBean;
    private String prefix;
    private DataSource datasource;
    private String moduleJpaEnabledPropertyPrefix;

    public RiceEntityManagerProxyFactoryBean(String prefix, DataSource datasource) {
        this.prefix = prefix;
        this.datasource = datasource;
        this.moduleJpaEnabledPropertyPrefix = prefix;
    }

    public RiceEntityManagerProxyFactoryBean(String prefix, DataSource datasource, String moduleJpaEnabledPropertyPrefix) {
        this.prefix = prefix;
        this.datasource = datasource;
        this.moduleJpaEnabledPropertyPrefix = moduleJpaEnabledPropertyPrefix;
    }

    public void afterPropertiesSet() throws Exception {
        /*if (OrmUtils.isJpaEnabled(moduleJpaEnabledPropertyPrefix)) {
			factoryBean = new RiceLocalContainerEntityManagerFactoryBean(prefix, datasource);
			factoryBean.afterPropertiesSet();
		}*/
    }

    public Class getObjectType() {
        return (factoryBean != null ? factoryBean.getObjectType() : EntityManagerFactory.class);
    }

    public Object getObject() throws Exception {
        return (factoryBean != null ? factoryBean.getObject() : null);
    }

    public boolean isSingleton() {
        return true;
    }

}
