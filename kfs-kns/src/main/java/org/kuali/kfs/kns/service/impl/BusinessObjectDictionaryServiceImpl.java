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
package org.kuali.kfs.kns.service.impl;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.exception.ReferenceAttributeNotAnOjbReferenceException;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.kns.datadictionary.InquiryDefinition;
import org.kuali.kfs.kns.datadictionary.InquirySectionDefinition;
import org.kuali.kfs.kns.datadictionary.LookupDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.kns.inquiry.InquiryAuthorizer;
import org.kuali.kfs.kns.inquiry.InquiryAuthorizerBase;
import org.kuali.kfs.kns.inquiry.InquiryPresentationController;
import org.kuali.kfs.kns.inquiry.InquiryPresentationControllerBase;
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.exception.IntrospectionException;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.valuefinder.ValueFinder;

/**
 * This class is the service implementation for the BusinessObjectDictionary.
 * This is the default, Kuali delivered implementation which leverages the
 * DataDictionaryService.
 */
@Deprecated
public class BusinessObjectDictionaryServiceImpl implements BusinessObjectDictionaryService {
	private static Logger LOG = Logger
			.getLogger(BusinessObjectDictionaryServiceImpl.class);

    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;

	public <T extends BusinessObject> InquiryAuthorizer getInquiryAuthorizer(
			Class<T> businessObjectClass) {
		Class inquiryAuthorizerClass = ((BusinessObjectEntry) getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObjectClass.getName())).getInquiryDefinition()
				.getAuthorizerClass();
		if (inquiryAuthorizerClass == null) {
			inquiryAuthorizerClass = InquiryAuthorizerBase.class;
		}
		try {
			return (InquiryAuthorizer) inquiryAuthorizerClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to instantiate InquiryAuthorizer class: "
							+ inquiryAuthorizerClass, e);
		}
	}

	public <T extends BusinessObject> InquiryPresentationController getInquiryPresentationController(
			Class<T> businessObjectClass) {
		Class inquiryPresentationControllerClass = ((BusinessObjectEntry) getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObjectClass.getName())).getInquiryDefinition()
				.getPresentationControllerClass();
		if (inquiryPresentationControllerClass == null) {
			inquiryPresentationControllerClass = InquiryPresentationControllerBase.class;
		}
		try {
			return (InquiryPresentationController) inquiryPresentationControllerClass
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to instantiate InquiryPresentationController class: "
							+ inquiryPresentationControllerClass, e);
		}
	}

    /**
     * Uses the DataDictionaryService.
     *
     * @see BusinessObjectDictionaryService#getBusinessObjectEntries()
     */
    public List getBusinessObjectClassnames() {
		return getDataDictionaryService().getDataDictionary()
				.getBusinessObjectClassNames();
    }

    /**
     * @see BusinessObjectDictionaryService#isLookupable(java.lang.Class)
     */
    public Boolean isLookupable(Class businessObjectClass) {
        Boolean isLookupable = Boolean.FALSE;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isLookupable = Boolean.valueOf(entry.hasLookupDefinition());
        }

        return isLookupable;
    }

    /**
     * @see BusinessObjectDictionaryService#isInquirable(java.lang.Class)
     */
    public Boolean isInquirable(Class businessObjectClass) {
        Boolean isInquirable = Boolean.FALSE;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isInquirable = Boolean.valueOf(entry.hasInquiryDefinition());
        }

        return isInquirable;
    }

    /**
     * @see BusinessObjectDictionaryService#isMaintainable(java.lang.Class)
     */
    public Boolean isMaintainable(Class businessObjectClass) {
        Boolean isMaintainable = Boolean.FALSE;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
			isMaintainable = Boolean
					.valueOf(getMaintenanceDocumentEntry(businessObjectClass) != null);
        }

        return isMaintainable;
    }


    /**
	 * @see BusinessObjectDictionaryService#isExportable(java.lang.Class)
	 */
	public Boolean isExportable(Class businessObjectClass) {
		Boolean isExportable = Boolean.FALSE;

		BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            isExportable = entry.getExporterClass() != null;
        }

        return isExportable;
	}

	/**
     * @see BusinessObjectDictionaryService#getLookupFieldNames(java.lang.Class)
     */
    public List getLookupFieldNames(Class businessObjectClass) {
        List results = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            results = lookupDefinition.getLookupFieldNames();
        }

        return results;
    }


    /**
     * @see BusinessObjectDictionaryService#getLookupTitle(java.lang.Class)
     */
    public String getLookupTitle(Class businessObjectClass) {
        String lookupTitle = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            lookupTitle = lookupDefinition.getTitle();
        }

        return lookupTitle;
    }

    /**
     * @see BusinessObjectDictionaryService#getLookupMenuBar(java.lang.Class)
     */
    public String getLookupMenuBar(Class businessObjectClass) {
        String menubar = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasMenubar()) {
                menubar = lookupDefinition.getMenubar();
            }
        }

        return menubar;
    }


    /**
     * @see BusinessObjectDictionaryService#getExtraButtonSource(java.lang.Class)
     */
    public String getExtraButtonSource(Class businessObjectClass) {
        String buttonSource = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasExtraButtonSource()) {
                buttonSource = lookupDefinition.getExtraButtonSource();
            }
        }

        return buttonSource;
    }

    /**
     * @see BusinessObjectDictionaryService#getExtraButtonParams(java.lang.Class)
     */
    public String getExtraButtonParams(Class businessObjectClass) {
        String buttonParams = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasExtraButtonParams()) {
                buttonParams = lookupDefinition.getExtraButtonParams();
            }
        }

        return buttonParams;
    }


    /**
     * @see BusinessObjectDictionaryService#getSearchIconOverride(java.lang.Class)
     */
    public String getSearchIconOverride(Class businessObjectClass) {
        String iconUrl = "";

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasSearchIconOverride()) {
                iconUrl = lookupDefinition.getSearchIconOverride();
            }
        }

        return iconUrl;
    }


    /**
     * @see BusinessObjectDictionaryService#getLookupDefaultSortFieldName(java.lang.Class)
     */
    public List<String> getLookupDefaultSortFieldNames(Class businessObjectClass) {
        List<String> defaultSort = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            if (lookupDefinition.hasDefaultSort()) {
				defaultSort = lookupDefinition.getDefaultSort()
						.getAttributeNames();
            }
        }
        if (defaultSort == null) {
            defaultSort = new ArrayList<String>();
        }

        return defaultSort;
    }

    /**
     * @see BusinessObjectDictionaryService#getLookupResultFieldNames(java.lang.Class)
     */
    public List<String> getLookupResultFieldNames(Class businessObjectClass) {
        List<String> results = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            results = lookupDefinition.getResultFieldNames();
        }

        return results;
    }

    /**
	 * @see BusinessObjectDictionaryService#getLookupResultFieldMaxLength(java.lang.Class,
	 *      java.lang.String)
     */
	public Integer getLookupResultFieldMaxLength(Class businessObjectClass,
			String resultFieldName) {
		Integer resultFieldMaxLength = null;

		LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
		if (lookupDefinition != null) {
			FieldDefinition field = lookupDefinition.getResultField(resultFieldName);
			if (field != null) {
				resultFieldMaxLength = field.getMaxLength();
			}
		}

		return resultFieldMaxLength;
    }

    /**
     * @see BusinessObjectDictionaryService#getLookupResultSetLimit(java.lang.Class)
     */
    public Integer getLookupResultSetLimit(Class businessObjectClass) {
        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if ( lookupDefinition != null ) {
			return lookupDefinition.getResultSetLimit(); // TODO: stupid, change
															// to return int
        } else {
            return null;
        }
    }

    /**
     * @see BusinessObjectDictionaryService#getMultipleValueLookupResultSetLimit(java.lang.Class)
     */
    public Integer getMultipleValueLookupResultSetLimit(Class businessObjectClass) {
        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if ( lookupDefinition != null ) {
            return lookupDefinition.getMultipleValuesResultSetLimit();
        } else {
            return null;
        }
    }

	/**
	 * @see BusinessObjectDictionaryService#getLookupNumberOfColumns(java.lang.Class)
	 */
	public Integer getLookupNumberOfColumns(Class businessObjectClass) {
		// default to 1
		int numberOfColumns = 1;

		LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
		if (lookupDefinition != null) {
			if (lookupDefinition.getNumOfColumns() > 1) {
				numberOfColumns = lookupDefinition.getNumOfColumns();
			}
		}

		return numberOfColumns;
	}

	/**
	 * @see BusinessObjectDictionaryService#getLookupAttributeRequired(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean getLookupAttributeRequired(Class businessObjectClass,
			String attributeName) {
        Boolean isRequired = null;

		FieldDefinition definition = getLookupFieldDefinition(
				businessObjectClass, attributeName);
        if (definition != null) {
            isRequired = Boolean.valueOf(definition.isRequired());
        }

        return isRequired;
    }

	/**
	 * @see BusinessObjectDictionaryService#getLookupAttributeReadOnly(java.lang.Class,
	 *      java.lang.String)
	 */
	public Boolean getLookupAttributeReadOnly(Class businessObjectClass, String attributeName) {
		Boolean readOnly = null;

		FieldDefinition definition = getLookupFieldDefinition(businessObjectClass, attributeName);
		if (definition != null) {
			readOnly = Boolean.valueOf(definition.isReadOnly());
		}

		return readOnly;
	}

	/**
	 * @see BusinessObjectDictionaryService#getInquiryFieldNames(java.lang.Class,
	 *      java.lang.String)
     */
	public List getInquiryFieldNames(Class businessObjectClass,
			String sectionTitle) {
        List results = null;

		InquirySectionDefinition inquirySection = getInquiryDefinition(
				businessObjectClass).getInquirySection(sectionTitle);
        if (inquirySection != null) {
            results = inquirySection.getInquiryFieldNames();
        }

        return results;
    }

    /**
     * @see BusinessObjectDictionaryService#getInquirySections(java.lang.Class)
     */
    public List<InquirySectionDefinition> getInquirySections(Class businessObjectClass) {
        List<InquirySectionDefinition> results = null;

		results = getInquiryDefinition(businessObjectClass)
				.getInquirySections();

        return results;
    }

    /**
     * @see BusinessObjectDictionaryService#getInquiryTitle(java.lang.Class)
     */
    public String getInquiryTitle(Class businessObjectClass) {
        String title = "";

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            title = inquiryDefinition.getTitle();
        }

        return title;
    }

    /**
     * @see BusinessObjectDictionaryService#getInquirableClass(java.lang.Class)
     */
    public Class getInquirableClass(Class businessObjectClass) {
        Class clazz = null;

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            clazz = inquiryDefinition.getInquirableClass();
        }

        return clazz;
    }

    /**
     * @see BusinessObjectDictionaryService#getMaintainableTitle(java.lang.Class)
     */
    public String getMaintainableLabel(Class businessObjectClass) {
        String label = "";

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(businessObjectClass);
        if (entry != null) {
            label = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(entry.getDocumentTypeName()).getLabel();
        }

        return label;
    }

    /**
     *
     * @see BusinessObjectDictionaryService#getLookupableID(java.lang.Class)
     */
    public String getLookupableID(Class businessObjectClass) {
        String lookupableID = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            lookupableID = lookupDefinition.getLookupableID();
        }

        return lookupableID;
    }


    /**
	 * Recurses down the updatable references and collections of a BO,
	 * uppercasing those attributes which are marked as needing to be uppercased
	 * in the data dictionary. Updatability of a reference or collection is
	 * defined by the PersistenceStructureService
     *
	 * @param bo
	 *            the BO to uppercase
     *
     * @see PersistenceStructureService#isCollectionUpdatable(Class, String)
     * @see PersistenceStructureService#isReferenceUpdatable(Class, String)
     * @see DataDictionaryService#getAttributeForceUppercase(Class, String)
     */
    public void performForceUppercase(BusinessObject bo) {
    	performForceUppercaseCycleSafe(bo, new HashSet<BusinessObject>());
    }

    /**
     * Handles recursion for performForceUppercase in a cycle-safe manner,
     * keeping track of visited BusinessObjects to prevent infinite recursion.
     */
    protected void performForceUppercaseCycleSafe(BusinessObject bo, Set<BusinessObject> visited) {
    	if (visited.contains(bo)) {
    		return;
    	} else {
    		visited.add(bo);
    	}
		PropertyDescriptor descriptors[] = PropertyUtils
				.getPropertyDescriptors(bo);
        for (int i = 0; i < descriptors.length; ++i) {
            try {
                if (descriptors[i] instanceof IndexedPropertyDescriptor) {
					// Skip this case because PropertyUtils.getProperty(bo,
					// descriptors[i].getName()) will throw a
                    // NoSuchMethodException on those. These
					// fields are usually convenience methods in the BO and in
					// the below code we anyway wouldn't know which index
                    // .toUpperCase().
				} else {
					Object nestedObject = ObjectUtils.getPropertyValue(bo,
							descriptors[i].getName());
					if (ObjectUtils.isNotNull(nestedObject)
							&& nestedObject instanceof BusinessObject) {
						if (persistenceStructureService
								.isPersistable(nestedObject.getClass())) {
                                try {
								if (persistenceStructureService.hasReference(bo
										.getClass(), descriptors[i].getName())) {
									if (persistenceStructureService
											.isReferenceUpdatable(
													bo.getClass(),
													descriptors[i].getName())) {
										if (persistenceStructureService
												.getForeignKeyFieldsPopulationState(
														(PersistableBusinessObject) bo,
														descriptors[i]
																.getName())
												.isAllFieldsPopulated()) {
											// check FKs to prevent probs caused
											// by referential integrity problems
                                            performForceUppercaseCycleSafe((BusinessObject) nestedObject, visited);
                                    }
                                    }
                                }
                                } catch (ReferenceAttributeNotAnOjbReferenceException ranaore) {
								LOG.debug("Propery " + descriptors[i].getName()
										+ " is not a foreign key reference.");
                                }
                            }
                    } else if (nestedObject instanceof String) {
						if (dataDictionaryService.isAttributeDefined(
								bo.getClass(), descriptors[i].getName())
								.booleanValue()
								&& dataDictionaryService
										.getAttributeForceUppercase(
												bo.getClass(),
												descriptors[i].getName())
										.booleanValue()) {
                            String curValue = (String) nestedObject;
							PropertyUtils.setProperty(bo, descriptors[i]
									.getName(), curValue.toUpperCase());
                        }
					} else {
						if (ObjectUtils.isNotNull(nestedObject)
								&& nestedObject instanceof Collection) {
							if (persistenceStructureService.hasCollection(bo
									.getClass(), descriptors[i].getName())) {
								if (persistenceStructureService
										.isCollectionUpdatable(bo.getClass(),
												descriptors[i].getName())) {
									Iterator iter = ((Collection) nestedObject)
											.iterator();
                            while (iter.hasNext()) {
                                Object collElem = iter.next();
                                if (collElem instanceof BusinessObject) {
											if (persistenceStructureService
													.isPersistable(collElem
															.getClass())) {
                                                performForceUppercaseCycleSafe((BusinessObject) collElem, visited);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
			} catch (IllegalAccessException e) {
				throw new IntrospectionException(
						"unable to performForceUppercase", e);
			} catch (InvocationTargetException e) {
				throw new IntrospectionException(
						"unable to performForceUppercase", e);
			} catch (NoSuchMethodException e) {
                // if the getter/setter does not exist, just skip over
				// throw new
				// IntrospectionException("unable to performForceUppercase", e);
            }
        }
    }

    /**
     * Sets the instance of the data dictionary service.
     *
     * @param dataDictionaryService
     */
	public void setDataDictionaryService(
			DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * This method retrieves the instance of the data dictionary service.
     *
     * @return An instance of the DataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    /**
     * @param businessObjectClass
	 * @return BusinessObjectEntry for the given dataObjectClass, or null if
	 *         there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
    private BusinessObjectEntry getBusinessObjectEntry(Class businessObjectClass) {
        validateBusinessObjectClass(businessObjectClass);

		BusinessObjectEntry entry = (BusinessObjectEntry) getDataDictionaryService()
				.getDataDictionary().getBusinessObjectEntry(
						businessObjectClass.getName());
        return entry;
    }

    /**
     * @param businessObjectClass
	 * @return MaintenanceDocumentEntry for the given dataObjectClass, or
	 *         null if there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
	private MaintenanceDocumentEntry getMaintenanceDocumentEntry(
			Class businessObjectClass) {
        validateBusinessObjectClass(businessObjectClass);

		MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry) getDataDictionaryService()
				.getDataDictionary()
				.getMaintenanceDocumentEntryForBusinessObjectClass(
						businessObjectClass);
        return entry;
    }

    /**
     * @param businessObjectClass
	 * @return LookupDefinition for the given dataObjectClass, or null if
	 *         there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
    private LookupDefinition getLookupDefinition(Class businessObjectClass) {
        LookupDefinition lookupDefinition = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            if (entry.hasLookupDefinition()) {
                lookupDefinition = entry.getLookupDefinition();
            }
        }

        return lookupDefinition;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
	 * @return FieldDefinition for the given dataObjectClass and lookup
	 *         field name, or null if there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
	private FieldDefinition getLookupFieldDefinition(Class businessObjectClass,
			String lookupFieldName) {
        if (StringUtils.isBlank(lookupFieldName)) {
			throw new IllegalArgumentException(
					"invalid (blank) lookupFieldName");
        }

        FieldDefinition fieldDefinition = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            fieldDefinition = lookupDefinition.getLookupField(lookupFieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
	 * @return FieldDefinition for the given dataObjectClass and lookup
	 *         result field name, or null if there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
	private FieldDefinition getLookupResultFieldDefinition(
			Class businessObjectClass, String lookupFieldName) {
        if (StringUtils.isBlank(lookupFieldName)) {
			throw new IllegalArgumentException(
					"invalid (blank) lookupFieldName");
        }

        FieldDefinition fieldDefinition = null;

        LookupDefinition lookupDefinition = getLookupDefinition(businessObjectClass);
        if (lookupDefinition != null) {
            fieldDefinition = lookupDefinition.getResultField(lookupFieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
	 * @return InquiryDefinition for the given dataObjectClass, or null if
	 *         there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
    private InquiryDefinition getInquiryDefinition(Class businessObjectClass) {
        InquiryDefinition inquiryDefinition = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            if (entry.hasInquiryDefinition()) {
                inquiryDefinition = entry.getInquiryDefinition();
            }
        }

        return inquiryDefinition;
    }


    /**
     * @see BusinessObjectDictionaryService#getTitleAttribute(java.lang.Class)
     */
    public String getTitleAttribute(Class businessObjectClass) {
        String titleAttribute = null;

        BusinessObjectEntry entry = getBusinessObjectEntry(businessObjectClass);
        if (entry != null) {
            titleAttribute = entry.getTitleAttribute();
        }

        return titleAttribute;
    }

    /**
     * @param businessObjectClass
     * @param attributeName
	 * @return FieldDefinition for the given dataObjectClass and field name,
	 *         or null if there is none
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
	private FieldDefinition getInquiryFieldDefinition(
			Class businessObjectClass, String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("invalid (blank) fieldName");
        }

        FieldDefinition fieldDefinition = null;

        InquiryDefinition inquiryDefinition = getInquiryDefinition(businessObjectClass);
        if (inquiryDefinition != null) {
            fieldDefinition = inquiryDefinition.getFieldDefinition(fieldName);
        }

        return fieldDefinition;
    }

    /**
     * @param businessObjectClass
	 * @throws IllegalArgumentException
	 *             if the given Class is null or is not a BusinessObject class
     */
    private void validateBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
			throw new IllegalArgumentException(
					"invalid (null) dataObjectClass");
        }
        if (!BusinessObject.class.isAssignableFrom(businessObjectClass)) {
			throw new IllegalArgumentException("class '"
					+ businessObjectClass.getName()
					+ "' is not a descendent of BusinessObject");
        }
    }

    /**
	 * @see BusinessObjectDictionaryService#forceLookupResultFieldInquiry(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean forceLookupResultFieldInquiry(Class businessObjectClass,
			String attributeName) {
        Boolean forceLookup = null;
        if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			forceLookup = Boolean.valueOf(getLookupResultFieldDefinition(
					businessObjectClass, attributeName).isForceInquiry());
        }

        return forceLookup;
    }

    /**
	 * @see BusinessObjectDictionaryService#noLookupResultFieldInquiry(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean noLookupResultFieldInquiry(Class businessObjectClass,
			String attributeName) {
        Boolean noLookup = null;
        if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			noLookup = Boolean.valueOf(getLookupResultFieldDefinition(
					businessObjectClass, attributeName).isNoInquiry());
        }

        return noLookup;
    }

    /**
	 * @see BusinessObjectDictionaryService#forceLookupFieldLookup(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean forceLookupFieldLookup(Class businessObjectClass,
			String attributeName) {
        Boolean forceLookup = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
			forceLookup = Boolean.valueOf(getLookupFieldDefinition(
					businessObjectClass, attributeName).isForceLookup());
        }

        return forceLookup;
    }

	public Boolean forceInquiryFieldLookup(Class businessObjectClass,
			String attributeName) {
        Boolean forceInquiry = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
			forceInquiry = Boolean.valueOf(getLookupFieldDefinition(
					businessObjectClass, attributeName).isForceInquiry());
        }

        return forceInquiry;
    }

    /**
	 * @see BusinessObjectDictionaryService#noLookupFieldLookup(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean noLookupFieldLookup(Class businessObjectClass,
			String attributeName) {
        Boolean noLookup = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
			noLookup = Boolean.valueOf(getLookupFieldDefinition(
					businessObjectClass, attributeName).isNoLookup());
        }

        return noLookup;
    }

    /**
	 * @see BusinessObjectDictionaryService#noLookupFieldLookup(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean noDirectInquiryFieldLookup(Class businessObjectClass,
			String attributeName) {
        Boolean noDirectInquiry = null;
        if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
			noDirectInquiry = Boolean.valueOf(getLookupFieldDefinition(
					businessObjectClass, attributeName).isNoDirectInquiry());
        }

        return noDirectInquiry;
    }

    /**
	 * @see BusinessObjectDictionaryService#getLookupResultFieldUseShortLabel(java.lang.Class,
	 *      java.lang.String)
	 */
	public Boolean getLookupResultFieldUseShortLabel(Class businessObjectClass,
			String attributeName) {
        Boolean useShortLabel = null;
        if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			useShortLabel = Boolean.valueOf(getLookupResultFieldDefinition(
					businessObjectClass, attributeName).isUseShortLabel());
        }

        return useShortLabel;
	}

	/**
	 * @see BusinessObjectDictionaryService#getLookupResultFieldTotal(java.lang.Class,
	 *      java.lang.String)
	 */
	public Boolean getLookupResultFieldTotal(Class businessObjectClass, String attributeName) {
		Boolean total = false;

		if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			total = Boolean.valueOf(getLookupResultFieldDefinition(
					businessObjectClass, attributeName).isTotal());
		}

		return total;
	}

	/**
	 * @see BusinessObjectDictionaryService#forceInquiryFieldInquiry(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean forceInquiryFieldInquiry(Class businessObjectClass,
			String attributeName) {
        Boolean forceInquiry = null;
        if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
			forceInquiry = Boolean.valueOf(getInquiryFieldDefinition(
					businessObjectClass, attributeName).isForceInquiry());
        }

        return forceInquiry;
    }

    /**
	 * @see BusinessObjectDictionaryService#noInquiryFieldInquiry(java.lang.Class,
	 *      java.lang.String)
     */
	public Boolean noInquiryFieldInquiry(Class businessObjectClass,
			String attributeName) {
        Boolean noInquiry = null;
        if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
			noInquiry = Boolean.valueOf(getInquiryFieldDefinition(
					businessObjectClass, attributeName).isNoInquiry());
        }

        return noInquiry;
    }

    /**
	 * @see BusinessObjectDictionaryService#getLookupFieldDefaultValue(java.lang.Class,
	 *      java.lang.String)
     */
	public String getLookupFieldDefaultValue(Class businessObjectClass,
			String attributeName) {
		return getLookupFieldDefinition(businessObjectClass, attributeName)
				.getDefaultValue();
    }

    /**
     * @see BusinessObjectDictionaryService#getLookupFieldDefaultValueFinderClass(java.lang.Class,
     *      java.lang.String)
     */
	public Class<? extends ValueFinder> getLookupFieldDefaultValueFinderClass(
			Class businessObjectClass, String attributeName) {
		return getLookupFieldDefinition(businessObjectClass, attributeName)
				.getDefaultValueFinderClass();
    }

	/** {@inheritDoc} */
	public String getLookupFieldQuickfinderParameterString(Class businessObjectClass, String attributeName) {
		return getLookupFieldDefinition(businessObjectClass, attributeName).getQuickfinderParameterString();
	}

	/** {@inheritDoc} */
	public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(Class businessObjectClass, String attributeName) {
		return getLookupFieldDefinition(businessObjectClass, attributeName).getQuickfinderParameterStringBuilderClass();
	}

	public void setPersistenceStructureService(
			PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

	/**
	 * @see BusinessObjectDictionaryService#isLookupFieldTreatWildcardsAndOperatorsAsLiteral(java.lang.Class, java.lang.String)
	 */
	public boolean isLookupFieldTreatWildcardsAndOperatorsAsLiteral(Class businessObjectClass, String attributeName) {
		FieldDefinition lookupFieldDefinition = getLookupFieldDefinition(businessObjectClass, attributeName);
		return lookupFieldDefinition != null && lookupFieldDefinition.isTreatWildcardsAndOperatorsAsLiteral();
	}

	/**
	 * @see BusinessObjectDictionaryService#getInquiryFieldAdditionalDisplayAttributeName(java.lang.Class,
	 *      java.lang.String)
	 */
	public String getInquiryFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName) {
		String additionalDisplayAttributeName = null;

		if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
			additionalDisplayAttributeName = getInquiryFieldDefinition(businessObjectClass, attributeName)
					.getAdditionalDisplayAttributeName();
		}

		return additionalDisplayAttributeName;
	}

	/**
	 * @see BusinessObjectDictionaryService#getInquiryFieldAlternateDisplayAttributeName(java.lang.Class,
	 *      java.lang.String)
	 */
	public String getInquiryFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName) {
		String alternateDisplayAttributeName = null;

		if (getInquiryFieldDefinition(businessObjectClass, attributeName) != null) {
			alternateDisplayAttributeName = getInquiryFieldDefinition(businessObjectClass, attributeName)
					.getAlternateDisplayAttributeName();
		}

		return alternateDisplayAttributeName;
	}

	/**
	 * @see BusinessObjectDictionaryService#getLookupFieldAdditionalDisplayAttributeName(java.lang.Class,
	 *      java.lang.String)
	 */
	public String getLookupFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName) {
		String additionalDisplayAttributeName = null;

		if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			additionalDisplayAttributeName = getLookupResultFieldDefinition(businessObjectClass, attributeName)
					.getAdditionalDisplayAttributeName();
		}

		return additionalDisplayAttributeName;
	}

	/**
	 * @see BusinessObjectDictionaryService#getLookupFieldAlternateDisplayAttributeName(java.lang.Class,
	 *      java.lang.String)
	 */
	public String getLookupFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName) {
		String alternateDisplayAttributeName = null;

		if (getLookupResultFieldDefinition(businessObjectClass, attributeName) != null) {
			alternateDisplayAttributeName = getLookupResultFieldDefinition(businessObjectClass, attributeName)
					.getAlternateDisplayAttributeName();
		}

		return alternateDisplayAttributeName;
	}

	/**
	 * @see BusinessObjectDictionaryService#tranlateCodesInLookup(java.lang.Class)
	 */
	public Boolean tranlateCodesInLookup(Class businessObjectClass) {
		boolean translateCodes = false;

		if (getLookupDefinition(businessObjectClass) != null) {
			translateCodes = getLookupDefinition(businessObjectClass).isTranslateCodes();
		}

		return translateCodes;
	}

	/**
	 * @see BusinessObjectDictionaryService#tranlateCodesInInquiry(java.lang.Class)
	 */
	public Boolean tranlateCodesInInquiry(Class businessObjectClass) {
		boolean translateCodes = false;

		if (getInquiryDefinition(businessObjectClass) != null) {
			translateCodes = getInquiryDefinition(businessObjectClass).isTranslateCodes();
		}

		return translateCodes;
	}

	/**
	 * @see BusinessObjectDictionaryService#isLookupFieldTriggerOnChange(java.lang.Class,
	 *      java.lang.String)
	 */
	public boolean isLookupFieldTriggerOnChange(Class businessObjectClass, String attributeName) {
		boolean triggerOnChange = false;
		if (getLookupFieldDefinition(businessObjectClass, attributeName) != null) {
			triggerOnChange = getLookupFieldDefinition(businessObjectClass, attributeName).isTriggerOnChange();
		}

		return triggerOnChange;
	}

	/**
	 * @see BusinessObjectDictionaryService#disableSearchButtonsInLookup(java.lang.Class)
	 */
	public boolean disableSearchButtonsInLookup(Class businessObjectClass) {
		boolean disableSearchButtons = false;

		if (getLookupDefinition(businessObjectClass) != null) {
			disableSearchButtons = getLookupDefinition(businessObjectClass).isDisableSearchButtons();
		}

		return disableSearchButtons;
	}



}
