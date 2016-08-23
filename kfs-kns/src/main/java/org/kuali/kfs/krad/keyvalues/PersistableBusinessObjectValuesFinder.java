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
package org.kuali.kfs.krad.keyvalues;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is a Generic ValuesFinder that builds the list of KeyValuePairs it returns
 * in getKeyValues() based on a BO along with a keyAttributeName and labelAttributeName
 * that are specified.
 */
@Transactional
public class PersistableBusinessObjectValuesFinder <T extends PersistableBusinessObject> extends org.kuali.kfs.krad.keyvalues.KeyValuesBase {

    private static final Log LOG = LogFactory.getLog(PersistableBusinessObjectValuesFinder.class);

    private Class<T> businessObjectClass;
    private String keyAttributeName;
    private String labelAttributeName;
    private boolean includeKeyInDescription = false;
    private boolean includeBlankRow = false;

    /**
     * Build the list of KeyValues using the key (keyAttributeName) and
     * label (labelAttributeName) of the list of all business objects found
     * for the BO class specified.
     *
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
	public List<KeyValue> getKeyValues() {
    	List<KeyValue> labels = new ArrayList<KeyValue>();

    	try {
    	    KeyValuesService boService = KRADServiceLocator.getKeyValuesService();
            Collection<T> objects = boService.findAll(businessObjectClass);
            if(includeBlankRow) {
            	labels.add(new ConcreteKeyValue("", ""));
            }
            for (T object : objects) {
            	Object key = PropertyUtils.getProperty(object, keyAttributeName);
            	String label = (String)PropertyUtils.getProperty(object, labelAttributeName);
            	if (includeKeyInDescription) {
            	    label = key + " - " + label;
            	}
            	labels.add(new ConcreteKeyValue(key.toString(), label));
    	    }
    	} catch (IllegalAccessException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("IllegalAccessException occurred while trying to build keyValues List. dataObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	} catch (InvocationTargetException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("InvocationTargetException occurred while trying to build keyValues List. dataObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	} catch (NoSuchMethodException e) {
            LOG.debug(e.getMessage(), e);
            LOG.error(e.getMessage());
            throw new RuntimeException("NoSuchMethodException occurred while trying to build keyValues List. dataObjectClass: " + businessObjectClass + "; keyAttributeName: " + keyAttributeName + "; labelAttributeName: " + labelAttributeName + "; includeKeyInDescription: " + includeKeyInDescription, e);
    	}

        return labels;
    }

    /**
     * @return the dataObjectClass
     */
    public Class<T> getBusinessObjectClass() {
        return this.businessObjectClass;
    }

    /**
     * @param businessObjectClass the dataObjectClass to set
     */
    public void setBusinessObjectClass(Class<T> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return the includeKeyInDescription
     */
    public boolean isIncludeKeyInDescription() {
        return this.includeKeyInDescription;
    }

    /**
     * @param includeKeyInDescription the includeKeyInDescription to set
     */
    public void setIncludeKeyInDescription(boolean includeKeyInDescription) {
        this.includeKeyInDescription = includeKeyInDescription;
    }

    /**
     * @return the keyAttributeName
     */
    public String getKeyAttributeName() {
        return this.keyAttributeName;
    }

    /**
     * @param keyAttributeName the keyAttributeName to set
     */
    public void setKeyAttributeName(String keyAttributeName) {
        this.keyAttributeName = keyAttributeName;
    }

    /**
     * @return the labelAttributeName
     */
    public String getLabelAttributeName() {
        return this.labelAttributeName;
    }

    /**
     * @param labelAttributeName the labelAttributeName to set
     */
    public void setLabelAttributeName(String labelAttributeName) {
        this.labelAttributeName = labelAttributeName;
    }

	/**
	 * @return the includeBlankRow
	 */
	public boolean isIncludeBlankRow() {
		return this.includeBlankRow;
	}

	/**
	 * @param includeBlankRow the includeBlankRow to set
	 */
	public void setIncludeBlankRow(boolean includeBlankRow) {
		this.includeBlankRow = includeBlankRow;
	}

}
