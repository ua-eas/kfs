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
package org.kuali.kfs.krad.keyvalues;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.framework.services.KimFrameworkServiceLocator;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class KimAttributeValuesFinder extends org.kuali.kfs.krad.keyvalues.KeyValuesBase {

	private static final Logger LOG = Logger.getLogger( KimAttributeValuesFinder.class );

	protected String kimTypeId;
	protected String kimAttributeName;
    private DataDictionaryService dataDictionaryService;

    protected DataDictionaryService getDataDictionaryService() {
		if ( dataDictionaryService == null ) {
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return this.dataDictionaryService;
	}

	/**
	 * @see org.kuali.kfs.krad.keyvalues.KeyValuesFinder#getKeyValues()
	 */
	@Override
	public List<KeyValue> getKeyValues() {
        KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
        if ( kimType != null ) {
	        KimTypeService service = KimFrameworkServiceLocator.getKimTypeService(kimType);
	        if ( service != null ) {
				return getAttributeValidValues(kimTypeId,kimAttributeName);
	        } 
	        LOG.error( "Unable to get type service " + kimType.getServiceName() );
        } else {
        	LOG.error( "Unable to obtain KIM type for kimTypeId=" + kimTypeId );
        }
        return Collections.emptyList();
	}

    private List<KeyValue> getAttributeValidValues(String kimTypeId, String attributeName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "getAttributeValidValues(" + kimTypeId + "," + attributeName + ")");
		}
		KimTypeAttribute attrib = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(attributeName);
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "Found Attribute definition: " + attrib );
		}
		List<KeyValue> pairs = null;
		if ( StringUtils.isNotBlank(attrib.getKimAttribute().getComponentName()) ) {
			try {
				Class.forName(attrib.getKimAttribute().getComponentName());
				try {
					pairs = getLocalDataDictionaryAttributeValues(attrib);
				} catch ( ClassNotFoundException ex ) {
					LOG.error( "Got a ClassNotFoundException resolving a values finder - since this should have been executing in the context of the host system - this should not happen.");
					return Collections.emptyList();
				}
			} catch ( ClassNotFoundException ex ) {
				LOG.error( "Got a ClassNotFoundException resolving a component name (" + attrib.getKimAttribute().getComponentName() + ") - since this should have been executing in the context of the host system - this should not happen.");
			}
		} else {
			pairs = getCustomValueFinderValues(attrib);
		}
        return pairs;
	}

    protected List<KeyValue> getCustomValueFinderValues(KimTypeAttribute attrib) {
		return Collections.emptyList();
	}

    protected List<KeyValue> getLocalDataDictionaryAttributeValues(KimTypeAttribute attr) throws ClassNotFoundException {

		BusinessObjectEntry entry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(attr.getKimAttribute().getComponentName());
		if ( entry == null ) {
			LOG.warn( "Unable to obtain BusinessObjectEntry for component name: " + attr.getKimAttribute().getComponentName() );
			return Collections.emptyList();
		}
		AttributeDefinition definition = entry.getAttributeDefinition(attr.getKimAttribute().getAttributeName());
		if ( definition == null ) {
			LOG.warn( "No attribute named " + attr.getKimAttribute().getAttributeName() + " found on BusinessObjectEntry for: " + attr.getKimAttribute().getComponentName() );
			return Collections.emptyList();
		}

        List<KeyValue> pairs = new ArrayList<KeyValue>();
		String keyValuesFinderName = definition.getControl().getValuesFinderClass();
		if ( StringUtils.isNotBlank(keyValuesFinderName)) {
			try {
				org.kuali.kfs.krad.keyvalues.KeyValuesFinder finder = (KeyValuesFinder)Class.forName(keyValuesFinderName).newInstance();
				if (finder instanceof PersistableBusinessObjectValuesFinder) {
	                ((PersistableBusinessObjectValuesFinder) finder).setBusinessObjectClass(
                            ClassLoaderUtils.getClass(definition.getControl().getBusinessObjectClass()));
	                ((PersistableBusinessObjectValuesFinder) finder).setKeyAttributeName(definition.getControl().getKeyAttribute());
	                ((PersistableBusinessObjectValuesFinder) finder).setLabelAttributeName(definition.getControl().getLabelAttribute());
	                if (definition.getControl().getIncludeBlankRow() != null) {
		                ((PersistableBusinessObjectValuesFinder) finder).setIncludeBlankRow(definition.getControl().getIncludeBlankRow());
	                }
	                ((PersistableBusinessObjectValuesFinder) finder).setIncludeKeyInDescription(definition.getControl().getIncludeKeyInLabel());
				}

                for (KeyValue pair : finder.getKeyValues()) {
                    pairs.add(new ConcreteKeyValue(pair));
                }

			} catch ( ClassNotFoundException ex ) {
				LOG.info( "Unable to find class: " + keyValuesFinderName + " in the current context." );
				throw ex;
			} catch (Exception e) {
				LOG.error("Unable to build a KeyValuesFinder for " + attr.getKimAttribute().getAttributeName(), e);
			}
		} else {
			LOG.warn( "No values finder class defined on the control definition (" + definition.getControl() + ") on BO / attr = " + attr.getKimAttribute().getComponentName() + " / " + attr.getKimAttribute().getAttributeName() );
		}
		return pairs;
	}

	/**
	 * @return the kimAttributeName
	 */
	public String getKimAttributeName() {
		return this.kimAttributeName;
	}

	/**
	 * @param kimAttributeName the kimAttributeName to set
	 */
	public void setKimAttributeName(String kimAttributeName) {
		this.kimAttributeName = kimAttributeName;
	}

	/**
	 * @return the kimTypeId
	 */
	public String getKimTypeId() {
		return this.kimTypeId;
	}

	/**
	 * @param kimTypeId the kimTypeId to set
	 */
	public void setKimTypeId(String kimTypeId) {
		this.kimTypeId = kimTypeId;
	}

}
