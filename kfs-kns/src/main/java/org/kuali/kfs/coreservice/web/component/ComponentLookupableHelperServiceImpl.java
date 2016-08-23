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
package org.kuali.kfs.coreservice.web.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.impl.component.ComponentBo;
import org.kuali.kfs.coreservice.impl.component.DerivedComponentBo;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collection;
import java.util.List;

public class ComponentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = -3978422770535345525L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ComponentLookupableHelperServiceImpl.class);

    private static final String ACTIVE = "active";
    private static final String CODE = "code";
    private static final String NAMESPACE_CODE = "namespaceCode";
    private static final String NAME = "name";

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {

        List<BusinessObject> baseLookup = (List<BusinessObject>) super.getSearchResults(fieldValues);

        String activeCheck = fieldValues.get(ACTIVE);
        if (activeCheck == null) {
            activeCheck = "";
        }
        int maxResultsCount = LookupUtils.getSearchResultsLimit(ComponentBo.class);
        // only bother with the component lookup if returning active components
        if (baseLookup instanceof CollectionIncomplete && !activeCheck.equals("N")) {
            long originalCount = Math.max(baseLookup.size(), ((CollectionIncomplete) baseLookup).getActualSizeIfTruncated());
            long totalCount = originalCount;

            Collection<DerivedComponentBo> derivedComponentBos = null;
            if (StringUtils.isBlank(fieldValues.get(CODE)) && StringUtils.isBlank(fieldValues.get(NAMESPACE_CODE))
                && StringUtils.isBlank(fieldValues.get(NAME))) {
                derivedComponentBos = KRADServiceLocator.getBusinessObjectService().findAll(DerivedComponentBo.class);
            } else {
                derivedComponentBos = getLookupService().findCollectionBySearchHelper(DerivedComponentBo.class, fieldValues, false);
            }
            if (CollectionUtils.isNotEmpty(derivedComponentBos)) {
                for (DerivedComponentBo derivedComponentBo : derivedComponentBos) {
                    if (totalCount++ < maxResultsCount) {
                        baseLookup.add(DerivedComponentBo.toComponentBo(derivedComponentBo));
                    } else {
                        break;
                    }
                }
            }

            if (totalCount > maxResultsCount) {
                ((CollectionIncomplete) baseLookup).setActualSizeIfTruncated(totalCount);
            } else {
                ((CollectionIncomplete) baseLookup).setActualSizeIfTruncated(0L);
            }
        }

        return baseLookup;
    }

    /**
     * Suppress the edit/copy links on synthetic detail types.
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (((ComponentBo) businessObject).getObjectId() == null) {
            return super.getEmptyActionUrls();
        }
        return super.getCustomActionUrls(businessObject, pkNames);
    }

}
