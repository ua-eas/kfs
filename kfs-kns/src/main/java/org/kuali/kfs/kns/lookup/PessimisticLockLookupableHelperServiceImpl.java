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
package org.kuali.kfs.kns.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.kns.util.FieldUtils;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.document.authorization.PessimisticLock;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.PessimisticLockService;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is the lookup helper for {@link PessimisticLock} objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PessimisticLockLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PessimisticLockLookupableHelperServiceImpl.class);

    private static final long serialVersionUID = -5839142187907211804L;
    private static final String OWNER_PRINCIPAL_ID_PROPERTY_NAME = "ownedByPrincipalIdentifier";
    private static final String OWNER_PRINCIPAL_NAME_PROPERTY_NAME = "ownedByUser.principalName";

    private List<Row> localRows;

    /**
     * Hides the applicable links when the PessimisticLock is not owned by the current user
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<org.kuali.kfs.kns.lookup.HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        PessimisticLock lock = (PessimisticLock)businessObject;
        if ( (lock.isOwnedByUser(GlobalVariables.getUserSession().getPerson())) || (KRADServiceLocatorWeb
                .getPessimisticLockService().isPessimisticLockAdminUser(GlobalVariables.getUserSession().getPerson())) ) {
            List<org.kuali.kfs.kns.lookup.HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
            anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.DELETE_METHOD, pkNames));
            return anchorHtmlDataList;
        } else {
            return super.getEmptyActionUrls();
        }
    }

    /**
     * This overridden method checks whether the user is an admin user according to {@link PessimisticLockService#isPessimisticLockAdminUser(Person)} and if the user is not an admin user the user field is set to Read Only and the lookup field
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (KRADServiceLocatorWeb.getPessimisticLockService().isPessimisticLockAdminUser(currentUser)) {
            return super.getRows();
        } else {
            if ( (ObjectUtils.isNull(localRows)) || localRows.isEmpty() ) {
                List<Field> fieldList = new ArrayList<Field>();
                int numColumns = -1;
                // hide a field and forcibly set a field
                for (Iterator<Row> iterator = super.getRows().iterator(); iterator.hasNext();) {
                    Row row = (Row) iterator.next();
                    if (numColumns == -1) {
                    	numColumns = row.getFields().size();
                    }
                    for (Iterator<Field> iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
                        Field field = (Field) iterator2.next();
                        if (!(KRADPropertyConstants.OWNED_BY_USER + "." + KimConstants.UniqueKeyConstants.PRINCIPAL_NAME).equals(field.getPropertyName()) &&
                        		!Field.BLANK_SPACE.equals(field.getFieldType())) {
                            fieldList.add(field);
                        }
                    }
                }
                // Since the removed field is the first one in the list, use FieldUtils to re-wrap the remaining fields accordingly.
                localRows = FieldUtils.wrapFields(fieldList, numColumns);
            }
            return localRows;
        }
    }

    /**
     * This method implementation is used to search for objects
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // remove hidden fields
        org.kuali.kfs.kns.lookup.LookupUtils.removeHiddenCriteriaFields(getBusinessObjectClass(), fieldValues);
        // force criteria if not admin user
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!KRADServiceLocatorWeb.getPessimisticLockService().isPessimisticLockAdminUser(currentUser)) {
            fieldValues.put(KRADPropertyConstants.OWNED_BY_PRINCIPAL_ID,GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }

        //set owner's principal id and remove owner principal name field 
        String principalName = fieldValues.get(OWNER_PRINCIPAL_NAME_PROPERTY_NAME);
        if (!StringUtils.isEmpty(principalName)) {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName);
            if (principal != null) { 
                fieldValues.put(OWNER_PRINCIPAL_ID_PROPERTY_NAME, principal.getPrincipalId());
            }
            fieldValues.remove(OWNER_PRINCIPAL_NAME_PROPERTY_NAME);
        }
        
        setBackLocation(fieldValues.get(KRADConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KRADConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KRADConstants.REFERENCES_TO_REFRESH));
        if (LOG.isInfoEnabled()) {
        	LOG.info("Search Criteria: " + fieldValues);
        }
        
        //replace principal name with principal id in fieldValues
        List searchResults;
        searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, true);
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        if (StringUtils.isNotEmpty((String)fieldValues.get(OWNER_PRINCIPAL_NAME_PROPERTY_NAME))) {
            Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName((String)fieldValues.get(OWNER_PRINCIPAL_NAME_PROPERTY_NAME));
            if (person == null) {
                String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), OWNER_PRINCIPAL_NAME_PROPERTY_NAME);
                GlobalVariables.getMessageMap().putError(OWNER_PRINCIPAL_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_EXISTENCE, attributeLabel);
            } 
        }
    }

}

