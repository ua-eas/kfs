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
package org.kuali.kfs.krad.datadictionary;

import org.springframework.beans.factory.InitializingBean;



/**
 * Common base class for DataDictionaryDefinition types.
 *
 *
 */
abstract public class DataDictionaryDefinitionBase implements DataDictionaryDefinition, InitializingBean {
    private static final long serialVersionUID = -2003626577498716712L;

	protected String id;

    public DataDictionaryDefinitionBase() {
    }

    public String getId() {
        return this.id;
    }

    /**
     * A unique identifier for this data dictionary element.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {}
}
